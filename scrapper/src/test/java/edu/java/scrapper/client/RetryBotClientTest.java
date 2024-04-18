package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.client.BotClient.BotClient;
import edu.java.model.LinkUpdate;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.spy;

@SpringBootTest
public class RetryBotClientTest extends IntegrationTest {
    private WireMockServer botApi;
    @Autowired
    private BotClient botClient;
    private BotClient clientMock;

    @BeforeEach
    void init() {
        botApi = new WireMockServer(8090);
        botApi.start();
        WireMock.configureFor("localhost", botApi.port());
        clientMock = spy(botClient);
    }

    @AfterEach
    void stopServer() {
        botApi.stop();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForPostUpdates(int httpCode) {
        LinkUpdate req = new LinkUpdate(
            1L,
            URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024"),
            "description",
            List.of(1L)
        );
        botApi.stubFor(WireMock.post(urlEqualTo("/updates"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode)
            )
        );

        Throwable ex = catchThrowable(() -> clientMock.retryPostUpdates(req));

        Mockito.verify(clientMock, Mockito.times(3)).postUpdates(req);
        Assertions.assertInstanceOf(WebClientResponseException.class, ex);
    }
}
