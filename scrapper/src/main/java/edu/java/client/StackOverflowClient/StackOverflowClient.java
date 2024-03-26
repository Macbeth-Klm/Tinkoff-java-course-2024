package edu.java.client.StackOverflowClient;

import edu.java.response.StackOverflowResponse;
import java.util.Optional;

public interface StackOverflowClient {
    Optional<StackOverflowResponse> fetchQuestionUpdates(long questionId);
}
