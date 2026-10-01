package com.ri.artificial.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ri.artificial.domain.po.ChatMessage;
import com.ri.artificial.mapper.ChatMessageMapper;
import com.ri.artificial.service.IChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-01 11:14
 */
@Service
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements IChatMessageService {

    @Override
    public void saveMessage(Integer userId, Integer historyId, String role, String content, String reasoning) {
        save(new ChatMessage()
                .setUserId(userId)
                .setHistoryId(historyId)
                .setRole(role)
                .setContent(content)
                .setReasoning(reasoning));
    }

    @Override
    public List<ChatMessage> listMessages(Integer userId, Integer historyId) {
        // 查询对应用户的聊天记录，最多返回 100 条
        return lambdaQuery()
                .eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getHistoryId, historyId)
                .orderByAsc(ChatMessage::getMessageId)
                .last("limit 100")
                .list();
    }

    @Override
    public void deleteByHistoryId(Integer userId, Integer historyId) {
        lambdaUpdate().eq(ChatMessage::getUserId, userId)
                .eq(ChatMessage::getHistoryId, historyId)
                .remove();
    }

    @Override
    public void deleteMessagesAfterTime(Integer userId, Integer messageId, Integer historyId) {

    }

    @Override
    public void reAnswerUserMessage(Integer userId, Integer messageId, Integer historyId) {

    }
}
