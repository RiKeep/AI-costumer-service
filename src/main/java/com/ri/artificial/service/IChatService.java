package com.ri.artificial.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ri.artificial.domain.dto.ChatRequest;
import com.ri.artificial.domain.po.ChatMessage;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

/**
 * @author Ri
 * @date 2026-10-01 11:12
 */
public interface IChatService {
    Flux<ServerSentEvent<String>> deepThinkChatCall(ChatRequest chatRequest, Integer userId);
}

