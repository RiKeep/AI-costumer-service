package com.ri.artificial.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ri
 * @date 2026-10-03 21:28
 */
@Data
public class ChatMessageVO {
    private Long messageId;
    private String role;
    private String reasoning;
    private String content;
    private LocalDateTime createTime;
}
