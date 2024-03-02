package edu.java.configuration;

import edu.java.client.BotClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BotClientConfig {
    @Value(value = "${api.bot.defaultUrl}")
    private String defaultScrapperUrl;

    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    BotClient botClient(WebClient.Builder webClientBuilder) {
        return new BotClient(defaultScrapperUrl, webClientBuilder);
    }
}
