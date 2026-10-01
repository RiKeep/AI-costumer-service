package com.ri.artificial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ri.artificial.domain.po.ChatHistory;
import com.ri.artificial.domain.po.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
public interface IChatMessageService extends IService<ChatMessage> {
    void saveMessage(Integer userId, Integer historyId, String role, String content, String reasoning);

    List<ChatMessage> listMessages(Integer userId, Integer historyId);

    /**
     * 按会话 id 删除消息（删除会话时级联调用）
     */
    void deleteByHistoryId(Integer userId, Integer historyId);

    /**
     *  todo 删除该时间下面的所有消息(删除该时间后面所有的会话)。
     */
    void deleteMessagesAfterTime(Integer userId, Integer messageId, Integer historyId);

    /**
     *  todo 重新回答该用户的问题(需要清空上一次回答记录)。
     */
    void reAnswerUserMessage(Integer userId, Integer messageId, Integer historyId);
}

