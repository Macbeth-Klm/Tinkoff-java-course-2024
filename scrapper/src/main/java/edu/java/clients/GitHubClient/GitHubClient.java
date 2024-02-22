package edu.java.clients.GitHubClient;

import edu.java.Responses.GitHubResponse;
import java.util.Optional;

public interface GitHubClient {
    Optional<GitHubResponse> fetchRepositoryEvents(String owner, String repo);
}
