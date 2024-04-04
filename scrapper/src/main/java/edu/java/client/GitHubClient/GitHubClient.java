package edu.java.client.GitHubClient;

import edu.java.response.GitHubResponse;
import java.util.Optional;

public interface GitHubClient {
    Optional<GitHubResponse> fetchRepositoryEvents(String owner, String repo);

    Optional<GitHubResponse> retryFetchRepositoryEvents(String owner, String repo);
}
