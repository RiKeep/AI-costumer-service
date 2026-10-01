package com.ri.artificial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ri.artificial.domain.po.ChatHistory;

import java.util.List;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
public interface IChatHistoryService extends IService<ChatHistory> {
    List<ChatHistory> queryChatHistory(Integer userId);

    /**
     * 删除单个会话（级联删除其消息）
     */
    void deleteChatHistory(Integer userId, Integer historyId);

    /**
     * 重命名会话标题
     */
    void renameChatHistory(Integer userId, Integer historyId, String title);

    void removeAllHistory(Integer userId);
}
