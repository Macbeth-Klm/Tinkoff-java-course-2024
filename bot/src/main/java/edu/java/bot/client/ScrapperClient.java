package edu.java.bot.client;

import edu.java.exception.ApiException;
import edu.java.model.AddLinkRequest;
import edu.java.model.ApiErrorResponse;
import edu.java.model.LinkResponse;
import edu.java.model.ListLinksResponse;
import edu.java.model.RemoveLinkRequest;
import java.util.Optional;
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

    public ScrapperClient(String baseUrl, WebClient.Builder builder) {
        webClient = builder.baseUrl(baseUrl).build();
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
}
