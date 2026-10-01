package com.ri.artificial.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ri.artificial.domain.dto.ChatRequest;
import com.ri.artificial.service.IChatHistoryService;
import com.ri.artificial.service.IChatMessageService;
import com.ri.artificial.service.IChatService;
import com.ri.artificial.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author Ri
 * @date 2026-10-01 11:14
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {

    private final IChatHistoryService chatHistoryService;
    private final IChatMessageService chatMessageService;
    private final IKnowledgeService knowledgeService;


    @Override
    public Flux<ServerSentEvent<String>> chatCall(ChatRequest chatRequest) {
        return null;
    }

    public void persistMessage(Integer userId, Integer historyId, String role, StringBuffer aiContent, StringBuffer aiReasoning) {
        String content = aiContent.toString();
        String reasoning = aiReasoning.toString();
        // 两段都为空说明尚未产生任何输出，不落库空消息
        if (StrUtil.isBlank(content) && StrUtil.isBlank(reasoning)) {
            return ;
        }
        chatMessageService.saveMessage(userId, historyId, role, content, reasoning);
    }
}
