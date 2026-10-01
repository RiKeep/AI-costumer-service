package com.ri.artificial.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Ri
 * @date 2026-10-01 11:19
 */
@Data
@Accessors(chain = true)
@TableName("sys_knowledge")
public class SysKnowledge {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 归属用户id（区分不同用户的知识库数据）
     */
    private Integer userId;
    // 向量数据库ID
    private String vectorId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private Integer chunkCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
