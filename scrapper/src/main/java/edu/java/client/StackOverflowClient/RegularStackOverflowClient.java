package edu.java.client.StackOverflowClient;

import edu.java.response.StackOverflowItem;
import edu.java.response.StackOverflowResponse;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class RegularStackOverflowClient implements StackOverflowClient {
    /*
    https://api.stackexchange.com/docs/answers-on-questions
    */
    private final WebClient webClient;

    public RegularStackOverflowClient(String baseUrl, WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Optional<StackOverflowResponse> fetchQuestionUpdates(long questionId) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/questions/{id}/answers")
                    .queryParam("order", "desc")
                    .queryParam("sort", "activity")
                    .queryParam("site", "stackoverflow")
                    .build(questionId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<StackOverflowItem>() {
                })
                .blockOptional()
                .map(items -> items.answers().getFirst());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
