package com.ri.artificial.splitter.impl;

import com.ri.artificial.domain.vo.SplitterOption;
import com.ri.artificial.domain.dto.SplitterForm;
import com.ri.artificial.splitter.DocumentSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-02 21:01
 */
@Component
public class TokenSplitter implements DocumentSplitter {
    @Override
    public String name() {
        return "Token分片";
    }

    @Override
    public String description() {
        return "按 token 数切分，Spring AI 原生，中英文差异大";
    }

    @Override
    public List<SplitterOption> options() {
        return List.of(
                new SplitterOption("chunkSize", "分片长度(token)", 500, 100, 2000)
        );
    }

    @Override
    public List<Document> split(List<Document> documents, SplitterForm params) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(params.getChunkSize())
                .build();
        return splitter.split(documents);
    }
}
