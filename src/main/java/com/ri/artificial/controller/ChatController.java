package com.ri.artificial.controller;

import com.ri.artificial.domain.dto.ChatRequest;
import com.ri.artificial.domain.vo.SseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author Ri
 * @date 2026-10-01 14:39
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/chat")
public class ChatController {
    private final ChatClient chatClient;

    @PostMapping("/call")
    public String chat(@RequestBody ChatRequest chatRequest) {
        return chatClient.prompt(chatRequest.getMessage())
                .call()
                .content();
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> flux(@RequestBody ChatRequest chatRequest) {
        return chatClient.prompt(chatRequest.getMessage())
                .stream()
                .content()
                .map(content -> ServerSentEvent.builder(new SseMessage("content", content).toJson()).build());
    }
}
