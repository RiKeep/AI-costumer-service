package com.ri.artificial.common;

import com.ri.artificial.domain.vo.SseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author Ri
 * @date 2026-10-01 22:24
 */
@Slf4j
@Component
public class SseStreamSupport {
    public Flux<ServerSentEvent<String>> wrap(
            Flux<ServerSentEvent<String>> source,
            Runnable onTerminate,
            String logTag ) {
        return source.doFinally(signal -> onTerminate.run())
                .onErrorResume(e -> handleError(e, logTag));
    }

    private Flux<ServerSentEvent<String>> handleError(Throwable e, String logTag) {
        if (e instanceof IOException) {
            log.warn("[{}] SSE 客户端连接中断: {}", logTag, e.getMessage());
            return Flux.empty();
        }
        log.error("[{}] AI 流式处理异常", logTag, e);
        return Flux.just(event("error", "生成失败，请重试"));
    }

    public ServerSentEvent<String> event(String type, String value) {
        return ServerSentEvent.builder(new SseMessage(type, value).toJson()).build();
    }
}
