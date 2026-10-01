package com.ri.artificial.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author Ri
 * @date 2026/10/01 15:02
 */
@Data
public class ChatRequest {
    @Schema(name = "sessionId", description = "会话Id")
    private String sessionId;
    @Schema(name = "message", description = "对话内容")
    private String message;
    @Schema(name = "deepThink", description = "深度思考")
    private Boolean deepThink;
    @Schema(name = "rag", description = "信息检索(RAG)")
    private Boolean rag;
    @Schema(name = "docIds", description = "选中的知识库文件ID列表，为空则检索全部")
    private List<Long> docIds;
    @Schema(name = "createTime", description = "客户端本地时间(yyyy-MM-dd HH:mm:ss)，作为本轮消息的落库时间；为空时由后端取当前时间")
    private String createTime;
}
