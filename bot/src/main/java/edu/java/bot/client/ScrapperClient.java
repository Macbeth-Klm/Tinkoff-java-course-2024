package edu.java.bot.client;

import org.springframework.web.reactive.function.client.WebClient;

public class ScrapperClient {
    private final WebClient webClient;

    public ScrapperClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
    }
}
