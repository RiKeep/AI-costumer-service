package com.ri.artificial.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ri.artificial.common.SseStreamSupport;
import com.ri.artificial.constant.MessageRole;
import com.ri.artificial.domain.dto.ChatRequest;
import com.ri.artificial.domain.po.ChatHistory;
import com.ri.artificial.service.IChatHistoryService;
import com.ri.artificial.service.IChatMessageService;
import com.ri.artificial.service.IChatService;
import com.ri.artificial.service.IKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ri
 * @date 2026-10-01 11:14
 */
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements IChatService {
    private final ChatClient chatClient;
    private final IChatHistoryService chatHistoryService;
    private final IChatMessageService chatMessageService;
    private final SseStreamSupport sseStreamSupport;
    private final IKnowledgeService knowledgeService;

    @Override
    public Flux<ServerSentEvent<String>> deepThinkChatCall(ChatRequest chatRequest, Integer userId) {
        // 获取会话记录，如果没有则创建新的会话记录
        ChatHistory history = chatHistoryService.getOrCreateChat(userId, chatRequest.getSessionId());

        // 获取当前会话历史上下文（从MySQL查询）
        List<Message> histories = chatMessageService.listMessages(userId, history.getHistoryId()).stream()
                .map(msg -> MessageRole.ASSISTANT.equals(msg.getRole())
                        ? new AssistantMessage(msg.getContent())
                        : new UserMessage(msg.getContent()))
                .collect(Collectors.toList());

        // 把用户的新消息加入到数据库中
        chatMessageService.saveMessage(userId, history.getHistoryId(), MessageRole.USER, chatRequest.getMessage(), null);

        // 接收AI思考过程
        StringBuilder aiReasoning = new StringBuilder();
        // 接收AI消息正文
        StringBuilder aiContent = new StringBuilder();

        Flux<ServerSentEvent<String>> stream = chatClient.prompt()
                .messages(histories)
                .user(chatRequest.getMessage())
                .stream()
                .chatResponse()
                .flatMap(resp -> {
                    Flux<ServerSentEvent<String>> eventFlux = Flux.empty();
                    AssistantMessage output = resp.getResult().getOutput();
                    // 思考内容
                    String reasoning = String.valueOf(output.getMetadata().get("reasoningContent"));

                    // 正文
                    String content = output.getText();
                    if (StrUtil.isNotBlank(content)) {
                        aiContent.append(content);
                        eventFlux = eventFlux.concatWithValues(sseStreamSupport.event("content", content));
                    }
                    if (StrUtil.isNotBlank(reasoning)) {
                        aiReasoning.append(reasoning);
                        eventFlux = eventFlux.concatWithValues(sseStreamSupport.event("reasoning", reasoning));
                    }
                    return eventFlux;
                });

        return sseStreamSupport.wrap(stream,
                () -> persistAssistantResponse(userId, history.getHistoryId(), aiContent, aiReasoning),
                "ChatService(DeepThinkChatCall)");
    }

    /**
     * 保存AI回复：正常完成、客户端取消、异常中断三条终止路径共用
     */
    private void persistAssistantResponse(Integer userId, Integer historyId, StringBuilder aiContent, StringBuilder aiReasoning) {
        String content = aiContent.toString();
        String reasoning = aiReasoning.toString();
        // 两段都为空说明尚未产生任何输出，不落库空消息
        if (StrUtil.isBlank(content) && StrUtil.isBlank(reasoning)) {
            return;
        }
        chatMessageService.saveMessage(userId, historyId, MessageRole.ASSISTANT, content, StrUtil.isBlank(reasoning) ? null : reasoning);
    }
}
