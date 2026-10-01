package com.ri.artificial.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Ri
 * @date 2026-10-01 11:15
 */
@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .temperature(0.1)
                .build();
        return ChatClient.builder(chatModel)
                .defaultOptions(chatOptions)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
