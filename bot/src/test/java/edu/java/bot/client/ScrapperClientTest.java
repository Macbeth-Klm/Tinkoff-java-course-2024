package edu.java.bot.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.LoggedResponse;
import edu.java.exceptions.ApiException;
import edu.java.models.AddLinkRequest;
import edu.java.models.LinkResponse;
import edu.java.models.ListLinksResponse;
import edu.java.models.RemoveLinkRequest;
import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

public class ScrapperClientTest {
    private WireMockServer scrapperApi;
    private ScrapperClient scrapperClient;
    private final WebClient.Builder builder = WebClient.builder();

    @BeforeEach
    void init() {
        scrapperApi = new WireMockServer(1338);
        scrapperApi.start();
        WireMock.configureFor("localhost", scrapperApi.port());
        scrapperClient = new ScrapperClient("http://localhost:" + scrapperApi.port(), builder);
    }

    @AfterEach
    void stopServer() {
        scrapperApi.stop();
    }

    @Test
    public void shouldReturnOkCodeForPostTgChatId() {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );

        scrapperClient.registerChat(id);
        LoggedResponse response = WireMock.getAllServeEvents().getFirst().getResponse();
        int responseStatus = response.getStatus();
        String responseBody = response.getBodyAsString();

        Assertions.assertAll(
            () -> Assertions.assertEquals(200, responseStatus),
            () -> Assertions.assertTrue(responseBody.isEmpty())
        );
    }

    @Test
    public void shouldReturnChatAlreadyExistForPostTgChatId() {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                       "description": "Пользователь уже зарегистрирован",
                       "code": "400",
                       "exceptionName": "BadRequestException",
                       "exceptionMessage": "User with the given chat id is already registered",
                       "stacktrace": [
                         "1"
                       ]
                     }
                    """)
            )
        );

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.registerChat(id),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Пользователь уже зарегистрирован", thrownException.getDescription()),
            () -> Assertions.assertEquals(
                "User with the given chat id is already registered",
                thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnInvalidHttpRequestForPostTgChatId() {
        Long id = -1L;
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/-1"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Некорректные параметры запроса",
                      "code": "400",
                      "exceptionName": "BadRequestException",
                      "exceptionMessage": "Invalid HTTP-request parameters",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)
            )
        );

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.registerChat(id),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Некорректные параметры запроса", thrownException.getDescription()),
            () -> Assertions.assertEquals("Invalid HTTP-request parameters", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnOkCodeForDeleteTgChatId() {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);

        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
            )
        );
        scrapperClient.deleteChat(id);

        LoggedResponse response = WireMock.getAllServeEvents().getLast().getResponse();
        int responseStatus = response.getStatus();
        String responseBody = response.getBodyAsString();

        Assertions.assertAll(
            () -> Assertions.assertEquals(200, responseStatus),
            () -> Assertions.assertTrue(responseBody.isEmpty())
        );
    }

    @Test
    public void shouldReturnNotFoundCodeForDeleteTgChatId() {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Чат не существует",
                      "code": "404",
                      "exceptionName": "NotFoundException",
                      "exceptionMessage": "User with the given chat id is not exist",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)
            )
        );

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.deleteChat(id),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Чат не существует", thrownException.getDescription()),
            () -> Assertions.assertEquals("User with the given chat id is not exist", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnInvalidHttpRequestForDeleteTgChatId() {
        Long id = -1L;
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/tg-chat/-1"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Некорректные параметры запроса",
                      "code": "400",
                      "exceptionName": "BadRequestException",
                      "exceptionMessage": "Invalid HTTP-request parameters",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)
            )
        );

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.deleteChat(id),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Некорректные параметры запроса", thrownException.getDescription()),
            () -> Assertions.assertEquals("Invalid HTTP-request parameters", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnOkCodeForPostLinks() {
        Long id = 1L;
        AddLinkRequest request =
            new AddLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"));
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "id": 1,
                      "url": "https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"
                    }
                    """)));

        LinkResponse response = scrapperClient.addLink(id, request).orElse(null);

        assert (response != null);
        Assertions.assertAll(
            () -> Assertions.assertEquals(1, response.id()),
            () -> Assertions.assertEquals(request.link(), response.url())
        );
    }

    @Test
    public void shouldReturnNotFoundCodeForPostLinks() {
        Long id = 1L;
        AddLinkRequest request =
            new AddLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"));
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Чат не существует",
                      "code": "404",
                      "exceptionName": "NotFoundException",
                      "exceptionMessage": "User with the given chat id is not exist",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)));

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.addLink(id, request),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Чат не существует", thrownException.getDescription()),
            () -> Assertions.assertEquals("User with the given chat id is not exist", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnInvalidHttpRequestForPostLinks() {
        Long id = 1L;
        AddLinkRequest request = new AddLinkRequest(null);
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Некорректные параметры запроса",
                      "code": "400",
                      "exceptionName": "BadRequestException",
                      "exceptionMessage": "Invalid HTTP-request parameters",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)));

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.addLink(id, request),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Некорректные параметры запроса", thrownException.getDescription()),
            () -> Assertions.assertEquals("Invalid HTTP-request parameters", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnOkCodeForDeleteLinks() {
        Long id = 1L;
        RemoveLinkRequest request =
            new RemoveLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"));
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "id": 1,
                      "url": "https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"
                    }
                    """)));
        scrapperClient.addLink(
            id,
            new AddLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"))
        );
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "id": 1,
                      "url": "https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"
                    }
                    """)));
        LinkResponse response = scrapperClient.deleteLink(id, request).orElse(null);

        assert (response != null);
        Assertions.assertAll(
            () -> Assertions.assertEquals(1, response.id()),
            () -> Assertions.assertEquals(request.link(), response.url())
        );
    }

    @Test
    public void shouldReturnNotFoundCodeForDeleteLinks() {
        Long id = 1L;
        RemoveLinkRequest request =
            new RemoveLinkRequest(URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023"));
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Ссылка не найдена",
                      "code": "404",
                      "exceptionName": "NotFoundException",
                      "exceptionMessage": "The user with the given chat id is not tracking this link",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)));

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.deleteLink(id, request),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Ссылка не найдена", thrownException.getDescription()),
            () -> Assertions.assertEquals(
                "The user with the given chat id is not tracking this link",
                thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnInvalidHttpRequestForDeleteLinks() {
        Long id = 1L;
        RemoveLinkRequest request = new RemoveLinkRequest(null);
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.delete(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Некорректные параметры запроса",
                      "code": "400",
                      "exceptionName": "BadRequestException",
                      "exceptionMessage": "Invalid HTTP-request parameters",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)));

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.deleteLink(id, request),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Некорректные параметры запроса", thrownException.getDescription()),
            () -> Assertions.assertEquals("Invalid HTTP-request parameters", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnOkCodeForGetLinks() {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.post(urlEqualTo("/tg-chat/1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200))
        );
        scrapperClient.registerChat(id);
        scrapperApi.stubFor(WireMock.get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "links": [],
                      "size": 0
                    }
                    """)));

        ListLinksResponse response = scrapperClient.getLinks(id).orElse(null);

        assert (response != null);

        Assertions.assertAll(
            () -> Assertions.assertEquals(0, response.size()),
            () -> Assertions.assertNotNull(response.links()),
            () -> Assertions.assertTrue(response.links().isEmpty())
        );
    }

    @Test
    public void shouldReturnNotFoundCodeForGetLinks() {
        Long id = 1L;
        scrapperApi.stubFor(WireMock.get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("1"))
            .willReturn(WireMock.aResponse()
                .withStatus(404)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                       "description": "Чат не существует",
                       "code": "404",
                       "exceptionName": "NotFoundException",
                       "exceptionMessage": "User with the given chat id is not exist",
                       "stacktrace": [
                         "1"
                       ]
                     }
                    """)));

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.getLinks(id),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Чат не существует", thrownException.getDescription()),
            () -> Assertions.assertEquals("User with the given chat id is not exist", thrownException.getMessage()
            )
        );
    }

    @Test
    public void shouldReturnInvalidHttpRequestForGetLinks() {
        Long id = -1L;
        scrapperApi.stubFor(WireMock.get(urlEqualTo("/links"))
            .withHeader("Tg-Chat-Id", WireMock.equalTo("-1"))
            .willReturn(WireMock.aResponse()
                .withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody("""
                    {
                      "description": "Некорректные параметры запроса",
                      "code": "400",
                      "exceptionName": "BadRequestException",
                      "exceptionMessage": "Invalid HTTP-request parameters",
                      "stacktrace": [
                        "1"
                      ]
                    }
                    """)));

        ApiException thrownException = catchThrowableOfType(
            () -> scrapperClient.getLinks(id),
            ApiException.class
        );

        Assertions.assertAll(
            () -> Assertions.assertEquals("Некорректные параметры запроса", thrownException.getDescription()),
            () -> Assertions.assertEquals("Invalid HTTP-request parameters", thrownException.getMessage()
            )
        );
    }
}
