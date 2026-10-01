package com.ri.artificial.domain.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ri
 * @date 2026-08-06 16:40
 */
@Data
@Accessors(chain = true)
public class KnowledgePageQuery {
    @Schema(name = "fileName", description = "文件名")
    private String fileName;
    @Schema(name = "userId", description = "归属用户id（服务端注入，用于用户隔离，前端不传）")
    private Integer userId;
}
