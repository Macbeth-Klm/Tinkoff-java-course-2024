package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.Responses.StackOverflowResponse;
import edu.java.clients.StackOverflowClient.RegularStackOverflowClient;
import edu.java.clients.StackOverflowClient.StackOverflowClient;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StackOverflowClientTest {
    private WireMockServer wireMockServer;
    private StackOverflowClient stackOverflowClient;
    private final WebClient.Builder webClientBuilder = WebClient.builder();

    @BeforeEach
    void initServerAndClient() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        stackOverflowClient =
            new RegularStackOverflowClient("http://localhost:" + wireMockServer.port(), webClientBuilder);
    }

    @AfterEach
    void stopServer() {
        wireMockServer.stop();
    }

    @Test
    public void shouldFetchLastAnswer() {
        long questionId = 21295883L;
        String responseBody = """
            {
              "items": [
                {
                  "owner": {
                    "account_id": 3835120,
                    "reputation": 330,
                    "user_id": 3179650,
                    "user_type": "registered",
                    "accept_rate": 20,
                    "profile_image": "https://www.gravatar.com/avatar/fb542e4a7945782096486149787a0b0f?s=256&d=identicon&r=PG&f=y&so-version=2",
                    "display_name": "Ian Schmitz",
                    "link": "https://stackoverflow.com/users/3179650/ian-schmitz"
                  },
                  "is_accepted": false,
                  "score": 1,
                  "last_activity_date": 1390432125,
                  "creation_date": 1390432125,
                  "answer_id": 21295993,
                  "question_id": 21295883,
                  "content_license": "CC BY-SA 3.0"
                },
                {
                  "owner": {
                    "account_id": 2845716,
                    "reputation": 968,
                    "user_id": 2444182,
                    "user_type": "registered",
                    "accept_rate": 77,
                    "profile_image": "https://www.gravatar.com/avatar/4df0fc1b3a014f93356d95d231dbcc7d?s=256&d=identicon&r=PG",
                    "display_name": "Chris Zhang",
                    "link": "https://stackoverflow.com/users/2444182/chris-zhang"
                  },
                  "is_accepted": true,
                  "score": 1,
                  "last_activity_date": 1390432065,
                  "creation_date": 1390432065,
                  "answer_id": 21295979,
                  "question_id": 21295883,
                  "content_license": "CC BY-SA 3.0"
                }
              ],
              "has_more": false,
              "quota_max": 10000,
              "quota_remaining": 9980
            }
            """;
        OffsetDateTime ExpectedLastActivityDate = Instant.ofEpochSecond(1390432125L).atOffset(ZoneOffset.UTC);
        Long expectedQuestionId = 21295883L;
        Long expectedAnswerId = 21295993L;
        String expectedOwnerName = "Ian Schmitz";
        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        StackOverflowResponse response = stackOverflowClient.fetchQuestionUpdates(questionId).orElse(null);

        Assertions.assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(ExpectedLastActivityDate, response.lastActivityDate()),
            () -> assertEquals(expectedQuestionId, response.questionId()),
            () -> assertEquals(expectedAnswerId, response.answerId()),
            () -> assertEquals(expectedOwnerName, response.owner().displayName())
        );
    }

    @Test
    public void shouldReturnEmptyResponseBecauseOfEmptyBody() {
        long questionId = 2129588121122121213L;
        String responseBody = """
            {
              "error_id": 400,
              "error_message": "ids",
              "error_name": "bad_parameter"
            }
            """;
        var uri = UriComponentsBuilder
            .fromPath("/questions/{id}/answers")
            .queryParam("order", "desc")
            .queryParam("sort", "activity")
            .queryParam("site", "stackoverflow")
            .uriVariables(Map.of("id", questionId));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = stackOverflowClient.fetchQuestionUpdates(questionId);

        assertThat(response).isNotPresent();
    }
}
