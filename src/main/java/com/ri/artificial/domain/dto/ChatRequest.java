package com.ri.artificial.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * @author Ri
 * @date 2026/10/01 15:02
 */
@Data
public class ChatRequest {
    private String sessionId;
    private String message;
    private Boolean deepThink;
    private Boolean rag;
    private List<Long> docIds;
    private String createTime;
}
