package com.ri.artificial.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ri.artificial.domain.dto.ChatRequest;
import com.ri.artificial.service.IChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/deep-think")
public class DeepThinkController {
    private final IChatService chatService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> flux(@RequestBody @Valid ChatRequest chatRequest) {
        return chatService.deepThinkChatCall(chatRequest, StpUtil.getLoginIdAsInt());
    }
}
