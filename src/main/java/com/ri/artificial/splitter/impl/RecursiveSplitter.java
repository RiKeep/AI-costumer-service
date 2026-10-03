package com.ri.artificial.splitter.impl;

import cn.hutool.core.util.StrUtil;
import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.domain.dto.SplitterForm;
import com.ri.artificial.splitter.DocumentSplitter;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Ri
 * @date 2026-10-02 21:15
 * 这是AI编码实现的
 * 递归字符分片：按 段落→行→句→词 逐级降级切分
 */
@Component
public class RecursiveSplitter implements DocumentSplitter {

    private static final List<String> SEPARATORS = List.of(
            "\n\n", "\n",
            "。", "！", "？", "；",
            ".", "!", "?", ";",
            "，", ",",
            " "
    );

    @Override
    public String name() { return "Recursive"; }

    @Override
    public String description() { return "按 段落→行→句→词 逐级切分，工业界默认方案"; }

    @Override
    public List<SplitterOption> options() {
        return List.of(
                new SplitterOption("chunkSize", "分片长度(字符)", 500, 100, 2000),
                new SplitterOption("overlap", "重叠长度(字符)", 80, 0, 500)
        );
    }

    @Override
    public List<Document> split(List<Document> documents, SplitterForm params) {
        List<Document> result = new ArrayList<>();
        for (Document doc : documents) {
            List<String> chunks = doSplit(doc.getText(), params.getChunkSize(), params.getOverlap(), 0);
            for (int i = 0; i < chunks.size(); i++) {
                Map<String, Object> meta = new HashMap<>(doc.getMetadata());
                meta.put("chunk_type", "recursive");
                meta.put("chunk_index", i);
                result.add(new Document(chunks.get(i), meta));
            }
        }
        return result;
    }


    private List<String> doSplit(String text, int chunkSize, int overlap, int sepIdx) {
        List<String> result = new ArrayList<>();
        if (text.isBlank()) {
            return result;
        }

        // 够短，直接返回
        if (text.length() <= chunkSize) {
            result.add(text.trim());
            return result;
        }

        // 分隔符用尽，硬切兜底
        if (sepIdx >= SEPARATORS.size()) {
            for (int i = 0; i < text.length(); i += Math.max(1, chunkSize - overlap)) {
                int end = Math.min(i + chunkSize, text.length());
                result.add(text.substring(i, end).trim());
                if (end == text.length()) {
                    break;
                }
            }
            return result;
        }

        String sep = SEPARATORS.get(sepIdx);
        String[] parts = text.split("(?<=" + Pattern.quote(sep) + ")");

        // 切不动，降级
        if (parts.length == 1) {
            return doSplit(text, chunkSize, overlap, sepIdx + 1);
        }

        StringBuilder current = new StringBuilder();
        for (String part : parts) {
            if (current.length() + part.length() > chunkSize && StrUtil.isNotBlank(current)) {
                result.add(current.toString().trim());
                String tail = current.length() > overlap
                        ? current.substring(current.length() - overlap)
                        : current.toString();
                current = new StringBuilder(tail);
            }

            if (part.length() > chunkSize) {
                if (StrUtil.isNotBlank(current)) {
                    result.add(current.toString().trim());
                    current = new StringBuilder();
                }
                result.addAll(doSplit(part, chunkSize, overlap, sepIdx + 1));
            } else {
                current.append(part);
            }
        }
        if (StrUtil.isNotBlank(current)) {
            result.add(current.toString().trim());
        }
        return result;
    }
}