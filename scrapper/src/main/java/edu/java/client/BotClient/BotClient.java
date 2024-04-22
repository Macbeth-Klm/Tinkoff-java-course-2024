package edu.java.client.BotClient;

import edu.java.exception.ApiException;
import edu.java.model.ApiErrorResponse;
import edu.java.model.LinkUpdate;
import edu.java.model.retry.BackoffType;
import edu.java.model.retry.RetryGenerator;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {
    private final WebClient webClient;
    @Value(value = "${api.bot.backoffType}")
    private BackoffType backoffType;
    @Value(value = "${api.bot.retryCount}")
    private int retryCount;
    @Value(value = "${api.bot.retryInterval}")
    private int retryInterval;
    @Value(value = "${api.bot.statuses}")
    private List<HttpStatus> statuses;
    private Retry retry;

    public BotClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses, "bot-client");
    }

    public void postUpdates(LinkUpdate req) {
        webClient.method(HttpMethod.POST)
            .uri("/updates")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .onStatus(
                HttpStatusCode::is4xxClientError,
                clientResponse -> clientResponse
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(String.class)
            .block();
    }

    public void retryPostUpdates(LinkUpdate req) {
        Retry.decorateRunnable(retry, () -> postUpdates(req)).run();
    }
}
