package edu.java.bot.client;

import edu.java.exception.ApiException;
import edu.java.model.AddLinkRequest;
import edu.java.model.ApiErrorResponse;
import edu.java.model.LinkResponse;
import edu.java.model.ListLinksResponse;
import edu.java.model.RemoveLinkRequest;
import edu.java.model.retry.BackoffType;
import edu.java.model.retry.RetryGenerator;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {
    private final String chatPath = "tg-chat/{id}";
    private final String linksUriPath = "/links";
    private final String tgChatIdHeaderName = "Tg-Chat-Id";
    private final WebClient webClient;
    @Value(value = "${api.scrapper.backoffType}")
    private BackoffType backoffType;
    @Value(value = "${api.scrapper.retryCount}")
    private int retryCount;
    @Value(value = "${api.scrapper.retryInterval}")
    private int retryInterval;
    @Value(value = "${api.scrapper.statuses}")
    private List<HttpStatus> statuses;
    private Retry retry;

    public ScrapperClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses, "scrapper-client");
    }

    public void registerChat(Long id) {
        webClient.method(HttpMethod.POST).uri(
                uriBuilder -> uriBuilder.path(chatPath).build(id)
            )
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve().onStatus(
                HttpStatusCode::is4xxClientError,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public void retryRegisterChat(Long id) {
        Retry.decorateRunnable(retry, () -> registerChat(id)).run();
    }

    public void deleteChat(Long id) {
        webClient.method(HttpMethod.DELETE).uri(
                uriBuilder -> uriBuilder.path(chatPath).build(id)
            )
            .contentType(MediaType.APPLICATION_JSON)
            .retrieve().onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(String.class)
            .blockOptional();
    }

    public void retryDeleteChat(Long id) {
        Retry.decorateRunnable(retry, () -> deleteChat(id)).run();
    }

    public Optional<ListLinksResponse> getLinks(Long id) {
        return webClient.method(HttpMethod.GET)
            .uri(linksUriPath)
            .contentType(MediaType.APPLICATION_JSON)
            .header(tgChatIdHeaderName, String.valueOf(id))
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(ListLinksResponse.class)
            .blockOptional();
    }

    public Optional<ListLinksResponse> retryGetLinks(Long id) {
        return Retry.decorateSupplier(retry, () -> getLinks(id)).get();
    }

    public Optional<LinkResponse> addLink(Long id, AddLinkRequest req) {
        return webClient.method(HttpMethod.POST)
            .uri(linksUriPath)
            .contentType(MediaType.APPLICATION_JSON)
            .header(tgChatIdHeaderName, String.valueOf(id))
            .bodyValue(req)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> retryAddLink(Long id, AddLinkRequest req) {
        return Retry.decorateSupplier(retry, () -> addLink(id, req)).get();
    }

    public Optional<LinkResponse> deleteLink(Long id, RemoveLinkRequest req) {
        return webClient.method(HttpMethod.DELETE)
            .uri(linksUriPath)
            .contentType(MediaType.APPLICATION_JSON)
            .header(tgChatIdHeaderName, String.valueOf(id))
            .bodyValue(req)
            .retrieve()
            .onStatus(
                HttpStatus.BAD_REQUEST::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .onStatus(
                HttpStatus.NOT_FOUND::equals,
                response -> response.bodyToMono(ApiErrorResponse.class)
                    .flatMap(apiErrorResponse -> Mono.error(new ApiException(
                        apiErrorResponse.exceptionMessage(),
                        apiErrorResponse.description()
                    )))
            )
            .bodyToMono(LinkResponse.class)
            .blockOptional();
    }

    public Optional<LinkResponse> retryDeleteLink(Long id, RemoveLinkRequest req) {
        return Retry.decorateSupplier(retry, () -> deleteLink(id, req)).get();
    }
}
