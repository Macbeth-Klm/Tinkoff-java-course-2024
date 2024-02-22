package edu.java.clients.StackOverflowClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.java.Responses.StackOverflowResponse;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

public class RegularStackOverflowClient implements StackOverflowClient {
    /*
    https://api.stackexchange.com/docs/answers-on-questions
    */
    @Value(value = "${api.stackoverflow.defaultUrl}")
    private String defaultUrl;
    private final WebClient webClient;

    public RegularStackOverflowClient() {
        webClient = WebClient.builder().baseUrl(defaultUrl).build();
    }

    public RegularStackOverflowClient(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public Optional<StackOverflowResponse> fetchQuestionUpdates(long questionId) {
        return Optional.ofNullable(webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/questions/{id}/answers")
                .queryParam("order", "desc")
                .queryParam("sort", "activity")
                .queryParam("site", "stackoverflow")
                .build(questionId))
            .retrieve()
            .bodyToMono(String.class)
            .mapNotNull(this::parseJson)
            .block());
    }

    public StackOverflowResponse parseJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode lastJsonAnswer = root.get("items").get(0);
            return objectMapper.treeToValue(lastJsonAnswer, StackOverflowResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}
