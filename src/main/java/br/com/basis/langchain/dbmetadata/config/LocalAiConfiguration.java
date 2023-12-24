package br.com.basis.langchain.dbmetadata.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.localai.LocalAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalAiConfiguration {
    @Bean
    public ChatLanguageModel chatLanguageModel(LocalAiProperties properties) {
        return LocalAiChatModel
                .builder()
                .modelName(properties.getModel())
                .timeout(properties.getTimeout())
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}
