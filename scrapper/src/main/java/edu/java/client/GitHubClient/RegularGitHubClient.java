package edu.java.client.GitHubClient;

import edu.java.model.retry.BackoffType;
import edu.java.model.retry.RetryGenerator;
import edu.java.response.GitHubResponse;
import io.github.resilience4j.retry.Retry;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

public class RegularGitHubClient implements GitHubClient {
    /*
    https://docs.github.com/en/rest/activity/events?apiVersion=2022-11-28#list-repository-events
     */
    private final WebClient webClient;
    @Value(value = "${api.github.backoffType}")
    private BackoffType backoffType;
    @Value(value = "${api.github.retryCount}")
    private int retryCount;
    @Value(value = "${api.github.retryInterval}")
    private int retryInterval;
    @Value(value = "${api.github.statuses}")
    private List<HttpStatus> statuses;
    private Retry retry;

    public RegularGitHubClient(String baseUrl, WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @PostConstruct
    private void initRetry() {
        retry = RetryGenerator.generate(backoffType, retryCount, retryInterval, statuses, "github-client");
    }

    @Override
    public Optional<GitHubResponse> fetchRepositoryEvents(String owner, String repo) {
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/repos/{owner}/{repo}/events")
                    .queryParam("per_page", 1)
                    .build(owner, repo))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<GitHubResponse>>() {
                })
                .blockOptional()
                .map(List::getFirst);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<GitHubResponse> retryFetchRepositoryEvents(String owner, String repo) {
        return Retry.decorateSupplier(retry, () -> fetchRepositoryEvents(owner, repo)).get();
    }
}
