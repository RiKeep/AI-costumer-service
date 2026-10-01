package com.ri.artificial.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author Ri
 * @date 2026/10/01 15:02
 */
@Data
public class ChatRequest {
    private String sessionId;
    @NotBlank(message = "消息不能为空")
    private String message;
}
