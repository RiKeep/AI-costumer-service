package com.ri.artificial.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @author Ri
 * @date 2026/10/01 15:02
 */
@Data
public class ChatRequest {
    /** sessionId 用来区分会话 */
    private String sessionId;
    @NotBlank(message = "消息不能为空")
    private String message;
    /** 深度思考：透出 reasoning 帧 */
    private boolean deepThink = false;
    /** 检索增强：挂上向量检索 */
    private boolean rag = false;
    /** 检索增强选中的知识库ids */
    private List<Long> docIds;
}
