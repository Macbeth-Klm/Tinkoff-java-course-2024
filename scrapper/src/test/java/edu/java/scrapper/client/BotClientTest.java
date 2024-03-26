package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import edu.java.client.BotClient.BotClient;
import edu.java.exceptions.ApiException;
import edu.java.models.LinkUpdate;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class BotClientTest {
    private WireMockServer botApi;
    private BotClient botClient;
    private final WebClient.Builder builder = WebClient.builder();

    @BeforeEach
    void init() {
        botApi = new WireMockServer(1337);
        botApi.start();
        WireMock.configureFor("localhost", botApi.port());
        botClient = new BotClient("http://localhost:" + botApi.port(), builder);
    }

    @AfterEach
    void stopServer() {
        botApi.stop();
    }

    @Test
    public void shouldReturnCorrectResponseWhenRequestIsValid() {
        LinkUpdate req = new LinkUpdate(
            1L,
            URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024"),
            "description",
            List.of(1L)
        );
        botApi.stubFor(WireMock.post(urlEqualTo("/updates"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
            )
        );

        botClient.postUpdates(req);
        LoggedResponse response = WireMock.getAllServeEvents().getFirst().getResponse();
        int responseStatus = response.getStatus();
        String responseBody = response.getBodyAsString();

        Assertions.assertAll(
            () -> Assertions.assertEquals(200, responseStatus),
            () -> Assertions.assertTrue(responseBody.isEmpty())
        );
    }

    @Test
    public void shouldReturnApiErrorResponseBecauseRequestIsInvalid() {
        LinkUpdate req = new LinkUpdate(
            1L,
            null,
            "description",
            List.of(1L)
        );
        botApi.stubFor(WireMock.post(urlEqualTo("/updates"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Некорректные параметры запроса",
                      "code": "400",
                      "exceptionName": "BotApiException",
                      "exceptionMessage": "Invalid HTTP-request parameters",
                      "stacktrace": [
                        "1",
                        "2"
                      ]
                    }
                    """)
            )
        );

        ApiException thrownException = catchThrowableOfType(
            () -> botClient.postUpdates(req),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Некорректные параметры запроса", thrownException.getDescription()),
            () -> Assertions.assertEquals("Invalid HTTP-request parameters", thrownException.getMessage())
        );
    }
}
