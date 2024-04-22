package edu.java.client.StackOverflowClient;

import edu.java.model.retry.BackoffType;
import edu.java.model.retry.RetryGenerator;
import edu.java.response.StackOverflowItem;
import edu.java.response.StackOverflowResponse;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class RegularStackOverflowClient implements StackOverflowClient {
    /*
    https://api.stackexchange.com/docs/answers-on-questions
    */
    private final WebClient webClient;
    @Value(value = "${api.stackoverflow.backoffType}")
    private BackoffType backoffType;
    @Value(value = "${api.stackoverflow.retryCount}")
    private int retryCount;
    @Value(value = "${api.stackoverflow.retryInterval}")
    private int retryInterval;
    @Value(value = "${api.stackoverflow.statuses}")
    private List<HttpStatus> statuses;
    private Retry retry;

    public RegularStackOverflowClient(String baseUrl, WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses, "stackoverflow-client");
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

    @Override
    public Optional<StackOverflowResponse> retryFetchQuestionUpdates(long questionId) {
        return Retry.decorateSupplier(retry, () -> fetchQuestionUpdates(questionId)).get();
    }
}
