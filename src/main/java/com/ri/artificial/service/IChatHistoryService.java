package com.ri.artificial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ri.artificial.domain.po.ChatHistory;
import com.ri.artificial.domain.po.ChatMessage;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
public interface IChatHistoryService extends IService<ChatHistory> {
    ChatHistory getOrCreateChat(Integer userId, String sessionId);

    ChatHistory getChatById(Integer userId, String sessionId);

    List<ChatHistory> queryChatHistory(Integer userId);

    void deleteChatHistory(Integer userId, Integer historyId);

    void renameChatHistory(Integer userId, Integer historyId, String title);

    void removeAllHistory(Integer userId);
}
