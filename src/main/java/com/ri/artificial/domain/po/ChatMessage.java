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
@TableName("chat_message")
public class ChatMessage {
    // 消息ID
    @TableId(type = IdType.AUTO)
    private Integer messageId;

    // 会话ID（关联 chat_history.history_id）
    private Integer historyId;

    // 用户ID
    private Integer userId;

    // 角色：user / assistant / system
    private String role;

    // 思考过程
    private String reasoning;

    // 消息正文
    private String content;

    // 创建时间
    private LocalDateTime createTime;

    // 更新时间
    private LocalDateTime updateTime;
}
