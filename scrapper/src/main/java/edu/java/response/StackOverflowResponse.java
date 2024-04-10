package edu.java.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StackOverflowResponse(
    Owner owner,
    @JsonProperty("last_activity_date")
    OffsetDateTime lastActivityDate,
    @JsonProperty("answer_id")
    long answerId,
    @JsonProperty("question_id")
    long questionId
) implements ResourceResponse {
    @Override
    public OffsetDateTime getUpdatedAt() {
        return lastActivityDate;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Owner(
        @JsonProperty("display_name")
        String displayName
    ) {
    }
}
