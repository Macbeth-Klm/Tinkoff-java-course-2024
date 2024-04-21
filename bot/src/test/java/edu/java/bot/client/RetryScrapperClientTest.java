package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import edu.java.model.AddLinkRequest;
import edu.java.model.RemoveLinkRequest;
import java.net.URI;
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
public class RetryScrapperClientTest {
    private WireMockServer scrapperApi;
    @Autowired
    private ScrapperClient scrapperClient;
    private ScrapperClient clientMock;

    @BeforeEach
    void init() {
        scrapperApi = new WireMockServer(8080);
        scrapperApi.start();
        WireMock.configureFor("localhost", scrapperApi.port());
        clientMock = spy(scrapperClient);
    }

    @AfterEach
    void stopServer() {
        scrapperApi.stop();
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForPostTgChatId(int httpCode) {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable ex = catchThrowable(() -> clientMock.retryRegisterChat(id));

        Mockito.verify(clientMock, Mockito.times(3)).registerChat(id);
        Assertions.assertInstanceOf(WebClientResponseException.class, ex);
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForDeleteTgChatId(int httpCode) {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable ex = catchThrowable(() -> clientMock.retryDeleteChat(id));

        Mockito.verify(clientMock, Mockito.times(3)).deleteChat(id);
        Assertions.assertInstanceOf(WebClientResponseException.class, ex);
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForGetLinks(int httpCode) {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable ex = catchThrowable(() -> clientMock.retryGetLinks(id));

        Mockito.verify(clientMock, Mockito.times(3)).getLinks(id);
        Assertions.assertInstanceOf(WebClientResponseException.class, ex);
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForPostLink(int httpCode) {
        Long id = 1L;
        AddLinkRequest req =
            new AddLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"));
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable ex = catchThrowable(() -> clientMock.retryAddLink(id, req));

        Mockito.verify(clientMock, Mockito.times(3)).addLink(id, req);
        Assertions.assertInstanceOf(WebClientResponseException.class, ex);
    }

    @ParameterizedTest
    @ValueSource(ints = {500, 502, 507, 503, 504})
    public void shouldCallThreeTimesRetryMethodForDeleteLink(int httpCode) {
        Long id = 1L;
        RemoveLinkRequest req =
            new RemoveLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"));
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(httpCode))
        );

        Throwable ex = catchThrowable(() -> clientMock.retryDeleteLink(id, req));

        Mockito.verify(clientMock, Mockito.times(3)).deleteLink(id, req);
        Assertions.assertInstanceOf(WebClientResponseException.class, ex);
    }
}
