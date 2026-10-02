package com.ri.artificial.splitter.impl;

import cn.hutool.json.JSONUtil;
import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.domain.dto.SplitterForm;
import com.ri.artificial.splitter.DocumentSplitter;
import lombok.RequiredArgsConstructor;
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
 * AI 分片：让 LLM 判断语义边界
 * 注意：只对短文本有效，长文本会截断或降级
 */
@Component
@RequiredArgsConstructor
public class AiSplitter implements DocumentSplitter {

    private final ChatClient chatClient;

    /** 单次送给 LLM 的最大字符数，超过就降级 */
    private static final int MAX_AI_INPUT = 3000;

    @Override
    public String name() { return "AI自动分片"; }

    @Override
    public String description() {
        return "让 LLM 按语义切分，效果最好但成本高，仅适合短文本";
    }

    @Override
    public List<SplitterOption> options() {
        return List.of(
                new SplitterOption("chunkSize", "目标分片长度(字符)", 500, 100, 2000),
                new SplitterOption("maxChunks", "最多分片数", 10, 1, 30)
        );
    }

    @Override
    public List<Document> split(List<Document> documents, SplitterForm params) {
        int chunkSize = params.getChunkSize();
        int maxChunks = params.getMaxChunks();

        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            String text = doc.getText();

            // 太长就降级：按 chunkSize 截断，避免 LLM 上下文爆炸
            if (text.length() > MAX_AI_INPUT) {
                text = text.substring(0, MAX_AI_INPUT);
            }

            List<String> chunks = callLlm(text, chunkSize, maxChunks);
            for (int i = 0; i < chunks.size(); i++) {
                Map<String, Object> meta = new HashMap<>(doc.getMetadata());
                meta.put("chunk_type", "ai");
                meta.put("chunk_index", i);
                result.add(new Document(chunks.get(i), meta));
            }
        }
        return result;
    }

    private List<String> callLlm(String text, int chunkSize, int maxChunks) {
        String prompt = """
                请将以下文本按语义完整性切分成不超过 %d 个片段，每个片段不超过 %d 字。
                要求：
                1. 每个片段必须是完整的语义单元，不要切断句子
                2. 保持原文内容完全不变，不要改写、不要总结
                3. 只返回 JSON 数组，格式：["片段1", "片段2", ...]
                4. 不要输出任何解释文字

                原文：
                %s
                """.formatted(maxChunks, chunkSize, text);

        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            // 解析 JSON 数组
            String json = extractJsonArray(response);
            return JSONUtil.toList(json, String.class);
        } catch (Exception e) {
            // LLM 失败降级：按 chunkSize 硬切
            List<String> fallback = new ArrayList<>();
            for (int i = 0; i < text.length(); i += chunkSize) {
                int end = Math.min(i + chunkSize, text.length());
                fallback.add(text.substring(i, end));
            }
            return fallback;
        }
    }

    private String extractJsonArray(String response) {
        int start = response.indexOf('[');
        int end = response.lastIndexOf(']');
        if (start < 0 || end < 0 || end <= start) {
            throw new IllegalStateException("LLM 未返回合法 JSON 数组");
        }
        return response.substring(start, end + 1);
    }
}
