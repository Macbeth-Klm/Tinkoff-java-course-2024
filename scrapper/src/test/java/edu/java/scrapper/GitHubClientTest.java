package edu.java.scrapper;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.Responses.GitHubResponse;
import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.clients.GitHubClient.RegularGitHubClient;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GitHubClientTest {
    private WireMockServer wireMockServer;
    private GitHubClient gitHubClient;

    @BeforeEach
    void initServerAndClient() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        gitHubClient = new RegularGitHubClient("http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void stopServer() {
        wireMockServer.stop();
    }

    @Test
    public void shouldFetchLastEvent() {
        String ownerName = "Macbeth-Klm";
        String repoName = "Tinkoff-java-course-2023";
        String responseBody = """
            [
              {
                "id": "34583047126",
                "type": "PushEvent",
                "actor": {
                  "id": 105516683,
                  "login": "Macbeth-Klm",
                  "display_login": "Macbeth-Klm",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/Macbeth-Klm",
                  "avatar_url": "https://avatars.githubusercontent.com/u/105516683?"
                },
                "repo": {
                  "id": 702672721,
                  "name": "Macbeth-Klm/Tinkoff-java-course-2023",
                  "url": "https://api.github.com/repos/Macbeth-Klm/Tinkoff-java-course-2023"
                },
                "payload": {
                  "repository_id": 702672721,
                  "push_id": 16509317811,
                  "size": 1,
                  "distinct_size": 1,
                  "ref": "refs/heads/main",
                  "head": "a72a2c6340832418e0552ad6258adc18dc79e96c",
                  "before": "0d6dfb0f385e4fb4f5feed8c519e1cd77ea311ba",
                  "commits": [
                    {
                      "sha": "a72a2c6340832418e0552ad6258adc18dc79e96c",
                      "author": {
                        "email": "popov02.ia@gmail.com",
                        "name": "Ilia Popov"
                      },
                      "message": "main: README files have been added for all projects and hw6 - hw11. Also .java files for projects have been added to the packages",
                      "distinct": true,
                      "url": "https://api.github.com/repos/Macbeth-Klm/Tinkoff-java-course-2023/commits/a72a2c6340832418e0552ad6258adc18dc79e96c"
                    }
                  ]
                },
                "public": true,
                "created_at": "2024-01-04T20:43:46Z"
              }
            ]
            """;
        Long expectedId = 34583047126L;
        String expectedType = "PushEvent";
        String expectedActorName = "Macbeth-Klm";
        String expectedRepoName = "Macbeth-Klm/Tinkoff-java-course-2023";
        OffsetDateTime expectedCreatedAt = OffsetDateTime.parse("2024-01-04T20:43:46Z");
        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner", ownerName,
                "repo", repoName
            ));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        GitHubResponse response = gitHubClient.fetchRepositoryEvents(ownerName, repoName).orElse(null);

        Assertions.assertAll(
            () -> assertNotNull(response),
            () -> assertEquals(expectedId, response.id()),
            () -> assertEquals(expectedType, response.type()),
            () -> assertEquals(expectedActorName, response.actor().login()),
            () -> assertEquals(expectedRepoName, response.repo().name()),
            () -> assertEquals(expectedCreatedAt, response.createdAt())
        );
    }

    @Test
    public void shouldReturnEmptyResponseBecauseOfEmptyBody() {
        String ownerName = "Macbeth-Klm";
        String repoName = "Tinkoff-java-course-2023";
        String responseBody = "[]";
        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner", ownerName,
                "repo", repoName
            ));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = gitHubClient.fetchRepositoryEvents(ownerName, repoName);

        assertThat(response).isNotPresent();
    }

    @Test
    public void shouldReturnEmptyResponseBecauseOfInvalidBodyFormat() {
        String ownerName = "Macbeth-Klm";
        String repoName = "Tinkoff-java-course-2023";
        String responseBody = "hw2 has been done!";
        var uri = UriComponentsBuilder
            .fromPath("/repos/{owner}/{repo}/events")
            .queryParam("per_page", 1)
            .uriVariables(Map.of(
                "owner", ownerName,
                "repo", repoName
            ));
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo(uri.toUriString()))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)
            )
        );

        var response = gitHubClient.fetchRepositoryEvents(ownerName, repoName);

        assertThat(response).isNotPresent();
    }

}
