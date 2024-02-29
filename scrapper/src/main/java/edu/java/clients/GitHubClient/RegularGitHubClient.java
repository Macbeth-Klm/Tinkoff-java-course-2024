package edu.java.clients.GitHubClient;

import edu.java.Responses.GitHubResponse;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class RegularGitHubClient implements GitHubClient {
    /*
    https://docs.github.com/en/rest/activity/events?apiVersion=2022-11-28#list-repository-events
     */
    private final WebClient webClient;

    public RegularGitHubClient(String baseUrl, WebClient.Builder webClientBuilder) {
        webClient = webClientBuilder.baseUrl(baseUrl).build();
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
}
