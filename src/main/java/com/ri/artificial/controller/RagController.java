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
 * @date 2026-10-01 14:41
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/rag")
public class RagController {
    private final ChatClient chatClient;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> flux(@RequestBody ChatRequest chatRequest) {
        return chatClient.prompt(chatRequest.getMessage())
                .stream()
                .content()
                .map(content -> ServerSentEvent.builder(new SseMessage("content", content).toJson()).build());
    }
}
