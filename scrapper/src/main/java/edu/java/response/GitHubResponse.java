package edu.java.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GitHubResponse(
    Long id,
    String type,
    Actor actor,
    Repo repo,
    @JsonProperty("created_at")
    OffsetDateTime createdAt
) implements ResourceResponse {
    @Override
    public OffsetDateTime getUpdatedAt() {
        return createdAt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Actor(
        String login
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Repo(
        String name
    ) {
    }
}
