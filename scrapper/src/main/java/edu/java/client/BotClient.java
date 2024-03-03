package edu.java.client;

import edu.java.exceptions.BotApiException;
import edu.java.models.ApiErrorResponse;
import edu.java.models.LinkUpdate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {
    private final WebClient webClient;

    public BotClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
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
                    .flatMap(apiErrorResponse -> Mono.error(new BotApiException(
                        "Некорректные параметры запроса"
                    )))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }
}
