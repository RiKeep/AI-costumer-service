package com.ri.artificial.splitter.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.ri.artificial.domain.dto.SplitterForm;
import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.splitter.DocumentSplitter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ri
 * @date 2026-10-02 21:41
 *
 * AI 分片：先用分隔符层级把原文切成细单元，再由 LLM 判断哪些相邻单元该合成一片，
 * 最后在本地按偏移从原文拼接。LLM 只输出编号区间、不产出任何文本，
 * 因此「切出的内容与原文一致」是结构性保证，不依赖事后校验。
 *
 * 单元粒度取目标片长的三分之一：切过头的还能由 LLM 合回来，切不够的补不回来。
 *
 * 文本较长时按 MAX_AI_INPUT 开窗送审。窗口内只定案前面的组，末尾一组留到下一窗口重判，
 * 使边界处的判断始终看得到完整的前后文；每个单元只被定案一次，无需事后调和冲突。
 *
 * 片长是软目标：LLM 的合并结果允许上浮到 SOFT_RATIO 倍，否则已识别出的语义组会被频繁否掉；
 * 超出硬上限的组在本地按单元边界再分。LLM 不可用时退回本地打包，得到一个确定且不差的结果。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiSplitter implements DocumentSplitter {

    private final ChatClient chatClient;

    /** 单元粒度 = 目标片长 / UNIT_DIVISOR */
    private static final int UNIT_DIVISOR = 3;

    /** 片长的容许上浮系数（软目标 → 硬上限） */
    private static final double SOFT_RATIO = 1.5;

    /** 片长硬上限；放宽前需确认 embedding 模型的最大输入长度 */
    private static final int MAX_CHUNK_CHARS = 2000;

    /** 单次送 LLM 的字符上限，同时也是送审窗口的上限 */
    private static final int MAX_AI_INPUT = 3000;

    /** 单元切分用的分隔符，由粗到细逐级降级，使任何内容都能落到某一级 */
    private static final List<String> SEPARATORS = List.of(
            "\n\n", "\n", "。", "！", "？", "；", ".", "!", "?", ";", "，", ",", " ");

    @Override
    public String name() { return "AI"; }

    @Override
    public String description() {
        return "先用分隔符层级切出细单元，再由 LLM 判断如何合并成语义片段，原文零改写";
    }

    @Override
    public List<SplitterOption> options() {
        return List.of(
                new SplitterOption("chunkSize", "目标分片长度(字符)", 500, 100, 2000)
        );
    }

    @Override
    public List<Document> split(List<Document> documents, SplitterForm params) {
        int chunkSize = params.getChunkSize();
        int unitSize = Math.max(1, chunkSize / UNIT_DIVISOR);
        int hardLimit = Math.min((int) (chunkSize * SOFT_RATIO), MAX_CHUNK_CHARS);

        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            String text = doc.getText();
            if (text == null || text.isBlank()) {
                continue;
            }

            int[] bounds = unitBounds(text, unitSize);
            int index = 0;
            for (int[] group : groups(text, bounds, chunkSize, hardLimit)) {
                // LLM 可能把语义组并得超过硬上限，这里再按单元边界切一次
                for (int[] piece : pack(bounds, group[0], group[1] + 1, hardLimit)) {
                    int start = bounds[piece[0]];
                    int end = bounds[piece[1] + 1];

                    Map<String, Object> meta = new HashMap<>(doc.getMetadata());
                    meta.put("chunk_type", "ai");
                    meta.put("chunk_index", index++);
                    meta.put("start", start);
                    meta.put("end", end);
                    result.add(new Document(text.substring(start, end), meta));
                }
            }
        }
        return result;
    }

    /**
     * 单元边界数组：第 i 个单元为 text[bounds[i], bounds[i + 1])，相邻单元首尾相接，
     * 拼接回去与原文逐字节相同。
     */
    private int[] unitBounds(String text, int unitSize) {
        List<int[]> units = new ArrayList<>();
        slice(text, 0, text.length(), unitSize, 0, units);

        int[] bounds = new int[units.size() + 1];
        for (int i = 0; i < units.size(); i++) {
            bounds[i] = units.get(i)[0];
            bounds[i + 1] = units.get(i)[1];
        }
        return bounds;
    }

    /**
     * 按分隔符层级把 [start, end) 切成不超过 unitSize 的单元。
     *
     * 用当前分隔符分段，段长不足 unitSize 的相邻段合并成一个单元；仍超标的段换更细的分隔符继续，
     * 全部用尽才按 unitSize 硬切。通用性来自这条降级链本身，因此不需要为文件类型写分支：
     * 散文走段落、逐行记录走行、整页无换行的走句、整页无标点的才硬切。
     */
    private void slice(String text, int start, int end, int unitSize, int sepIndex, List<int[]> out) {
        if (start >= end) {
            return;
        }
        if (end - start <= unitSize) {
            out.add(new int[]{start, end});
            return;
        }
        if (sepIndex >= SEPARATORS.size()) {
            for (int at = start; at < end; at += unitSize) {
                out.add(new int[]{at, Math.min(at + unitSize, end)});
            }
            return;
        }

        List<Integer> cuts = cutPoints(text, start, end, SEPARATORS.get(sepIndex));
        cuts.add(end);

        int unitStart = start;
        int pieceStart = start;
        for (int cut : cuts) {
            if (cut - pieceStart > unitSize) {
                // 这一段本身就超标，交给更细的分隔符处理，不与相邻段合并
                if (pieceStart > unitStart) {
                    out.add(new int[]{unitStart, pieceStart});
                }
                slice(text, pieceStart, cut, unitSize, sepIndex + 1, out);
                unitStart = cut;
            } else if (cut - unitStart > unitSize) {
                // 再并进来就超标，先把已攒够的收成一个单元
                out.add(new int[]{unitStart, pieceStart});
                unitStart = pieceStart;
            }
            pieceStart = cut;
        }
        if (unitStart < end) {
            out.add(new int[]{unitStart, end});
        }
    }

    /** [start, end) 内某分隔符的所有切点（切在该分隔符之后，即该段的结束位置） */
    private List<Integer> cutPoints(String text, int start, int end, String separator) {
        List<Integer> cuts = new ArrayList<>();
        int at = text.indexOf(separator, start);
        while (at >= 0 && at + separator.length() <= end) {
            cuts.add(at + separator.length());
            at = text.indexOf(separator, at + separator.length());
        }
        return cuts;
    }

    /**
     * 用 LLM 把单元合并成语义片段，返回单元下标区间（[start, end] 含两端）。
     *
     * 窗口内只定案前面的组，末尾一组留到下一窗口重判，这样跨窗口的边界不会被草率切开；
     * 每个单元只被定案一次。LLM 不可用时退回本地按目标长度打包。
     */
    private List<int[]> groups(String text, int[] bounds, int chunkSize, int hardLimit) {
        List<int[]> result = new ArrayList<>();
        int total = bounds.length - 1;
        int from = 0;

        while (from < total) {
            int to = from + 1;
            while (to < total && bounds[to + 1] - bounds[from] <= MAX_AI_INPUT) {
                to++;
            }
            boolean last = to >= total;

            // 整个窗口本身就在目标长度内，无需送审
            if (bounds[to] - bounds[from] <= chunkSize) {
                result.add(new int[]{from, to - 1});
                from = to;
                continue;
            }

            List<int[]> local = askModel(text, bounds, from, to, chunkSize, hardLimit);
            if (local == null) {
                result.addAll(pack(bounds, from, to, chunkSize));
                from = to;
                continue;
            }

            int decide = last ? local.size() : local.size() - 1;
            for (int i = 0; i < decide; i++) {
                result.add(new int[]{from + local.get(i)[0], from + local.get(i)[1]});
            }

            if (last) {
                from = total;
            } else {
                int next = from + local.get(local.size() - 1)[0];
                if (next <= from) {
                    // 挂起组占满整个窗口，再挂起就无法推进：本窗口全部定案
                    result.add(new int[]{from + local.get(local.size() - 1)[0], from + local.get(local.size() - 1)[1]});
                    from = to;
                } else {
                    from = next;
                }
            }
        }
        return result;
    }

    /** 送审一个窗口，返回局部单元下标区间（0 基含两端）；方案不可用时返回 null */
    private List<int[]> askModel(String text, int[] bounds, int from, int to, int chunkSize, int hardLimit) {
        int count = to - from;
        StringBuilder list = new StringBuilder();
        for (int i = from; i < to; i++) {
            list.append(i - from + 1).append(". ").append(text, bounds[i], bounds[i + 1]);
            if (text.charAt(bounds[i + 1] - 1) != '\n') {
                list.append('\n');
            }
        }

        String prompt = """
                下面是按原文顺序切出的 1..%d 个文本单元。请给出分片方案：把相邻单元合并成若干片段，
                使每片语义完整、长度接近 %d 字且不超过 %d 字。

                要求：
                1. 每个片段用 [起始编号, 结束编号] 表示，含两端
                2. 首尾相接、不重叠、不遗漏，正好覆盖 1..%d
                3. 只能合并相邻单元，不要拆开单元，也不要把不相干的内容并成一片
                4. 只输出 JSON 二维数组，不要任何解释。示例：[[1,5],[6,12]]

                文本单元：
                %s
                """.formatted(count, chunkSize, hardLimit, count, list);

        try {
            String response = chatClient.prompt().user(prompt).call().content();
            return parseRanges(response, count);
        } catch (Exception e) {
            log.warn("AI 分片：单元区间 [{}, {}) 未取得有效方案（{}），退回按目标长度打包", from, to, e.getMessage());
            return null;
        }
    }

    /**
     * 解析编号区间，转成 0 基含两端的局部下标。
     * 越界、倒置、重叠一律判为非法返回 null；尾部缺口并入末组，保证覆盖到最后不丢内容。
     */
    private List<int[]> parseRanges(String response, int count) {
        if (response == null) {
            return null;
        }
        int open = response.indexOf('[');
        int close = response.lastIndexOf(']');
        if (open < 0 || close <= open) {
            return null;
        }

        JSONArray array = JSONUtil.parseArray(response.substring(open, close + 1));
        List<int[]> result = new ArrayList<>();
        int cursor = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONArray pair = array.getJSONArray(i);
            if (pair == null || pair.size() != 2) {
                return null;
            }
            int start = pair.getInt(0) - 1;
            int end = pair.getInt(1) - 1;
            if (start < cursor || end < start || end >= count) {
                return null;
            }
            result.add(new int[]{start, end});
            cursor = end + 1;
        }
        if (result.isEmpty()) {
            return null;
        }
        if (cursor < count) {
            result.get(result.size() - 1)[1] = count - 1;
        }
        return result;
    }

    /**
     * 把 [from, to) 的单元按 limit 贪心打包，返回含两端的单元区间。
     * 单元已是最小语义单元，因此单个单元本身就超限时让它自成一片，不再从中间切开。
     */
    private List<int[]> pack(int[] bounds, int from, int to, int limit) {
        List<int[]> result = new ArrayList<>();
        int start = from;
        for (int i = from + 1; i < to; i++) {
            if (bounds[i + 1] - bounds[start] > limit) {
                result.add(new int[]{start, i - 1});
                start = i;
            }
        }
        result.add(new int[]{start, to - 1});
        return result;
    }
}
