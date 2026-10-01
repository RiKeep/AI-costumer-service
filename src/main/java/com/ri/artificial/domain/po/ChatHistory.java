package com.ri.artificial.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author Ri
 * @date 2026-10-01 11:19
 */
@Data
@Accessors(chain = true)
@TableName("chat_history")
public class ChatHistory {
    // 会话ID
    @TableId(type = IdType.AUTO)
    private Integer historyId;

    // 唯一UUID
    private String historyUuid;

    // 用户ID
    private Integer userId;

    // 会话标题
    private String title;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
