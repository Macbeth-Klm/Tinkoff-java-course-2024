package edu.java.client;

import org.springframework.web.reactive.function.client.WebClient;

public class BotClient {
    private final WebClient webClient;

    public BotClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
    }
}
