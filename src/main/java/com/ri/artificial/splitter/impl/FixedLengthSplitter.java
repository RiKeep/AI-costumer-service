package com.ri.artificial.splitter.impl;

import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.domain.dto.SplitterForm;
import com.ri.artificial.splitter.DocumentSplitter;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ri
 * @date 2026-10-02 21:05
 */
@Component
public class FixedLengthSplitter implements DocumentSplitter {

    @Override
    public String name() {
        return "固定字符分片";
    }

    @Override
    public String description() {
        return "按字符数硬切，会切断语义（仅用于对比）";
    }

    @Override
    public List<SplitterOption> options() {
        return List.of(
                new SplitterOption("chunkSize", "分片长度(字符)", 500, 100, 2000),
                new SplitterOption("overlap", "重叠长度(字符)", 50, 0, 500)
        );
    }

    @Override
    public List<Document> split(List<Document> documents, SplitterForm params) {
        List<Document> result = new ArrayList<>();
        for (Document document : documents) {
            String text = document.getText();
            int start = 0;
            while (start < text.length()) {
                int end = Math.min(start + params.getChunkSize(), text.length());
                String chunk = text.substring(start, end);

                Map<String, Object> meta = new HashMap<>(document.getMetadata());
                meta.put("chunk_type", "fixed");
                meta.put("start", start);
                meta.put("end", end);

                result.add(new Document(chunk, meta));
                if (end == text.length()) {
                    break;
                }
                start = end - params.getOverlap();
            }
        }
        return result;
    }
}
