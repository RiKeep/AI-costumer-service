package com.ri.artificial.controller;

import cn.hutool.core.util.StrUtil;
import com.ri.artificial.domain.dto.ChatRequest;
import com.ri.artificial.domain.vo.SseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/deep-think")
public class DeepThinkController {
    private final ChatClient chatClient;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> flux(@RequestBody ChatRequest chatRequest) {
        // 接收AI思考过程
        StringBuilder aiReasoning = new StringBuilder();
        // 接收AI消息正文
        StringBuilder aiContent = new StringBuilder();
        return chatClient.prompt(chatRequest.getMessage())
                .stream()
                .chatResponse()
                .flatMap(resp -> {
                    Flux<ServerSentEvent<String>> eventFlux = Flux.empty();
                    AssistantMessage output = resp.getResult().getOutput();
                    // 思考内容
                    String reasoning = String.valueOf(output.getMetadata().get("reasoningContent"));

                    // 正文
                    String content = output.getText();
                    if(StrUtil.isNotBlank(content)){
                        aiContent.append(content);
                        eventFlux = eventFlux.concatWithValues(sseEvent("content", content));
                    }
                    if (StrUtil.isNotBlank(reasoning)) {
                        aiReasoning.append(reasoning);
                        eventFlux = eventFlux.concatWithValues(sseEvent("reasoning", reasoning));
                    }
                    return eventFlux;
                });
    }

    /**
     * 深度思考事件包装：思考过程与正式回答分通道，前端按 type 分流渲染
     */
    private ServerSentEvent<String> sseEvent(String type, String value){
        return ServerSentEvent.builder(new SseMessage(type, value).toJson()).build();
    }
}
