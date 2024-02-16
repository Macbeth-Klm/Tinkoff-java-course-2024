package edu.java.bot;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import edu.java.bot.processor.UserMessageProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProcessorTest {
    private static final long CHAT_ID = 15L;
    @Mock
    Update update;
    @Mock
    Message message;
    @Mock
    Chat chat;

    @Autowired
    UserMessageProcessor processor;

    @Autowired
    DatabaseImitation database;

    @Test
    public void shouldReturnCorrectAnswersWhenUserTypesMessage() {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(update.message().chat().id()).thenReturn(CHAT_ID);

        Mockito.when(update.message().text()).thenReturn("/start");
        SendMessage firstAnswer = processor.process(update);
        Mockito.when(update.message().text()).thenReturn("/help");
        SendMessage secondAnswer = processor.process(update);
        Mockito.when(update.message().text()).thenReturn("/list");
        SendMessage thirdAnswer = processor.process(update);
        Mockito.when(update.message().text())
            .thenReturn("/track https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        SendMessage fourthAnswer = processor.process(update);
        Mockito.when(update.message().text())
            .thenReturn("/untrack https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        SendMessage fifthAnswer = processor.process(update);

        Assertions.assertAll(
            () -> Assertions.assertEquals(
                CHAT_ID,
                fifthAnswer.getParameters().get("chat_id")
            ),
            () -> Assertions.assertEquals(
                "Вы успешно зарегистрированы!",
                firstAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                """
                    Список команд:
                    /start - Зарегистрироваться на Link_Tracker_Bot
                    /list - Ваши подписки
                    /track - Подписаться
                    /untrack - Отписаться
                    """,
                secondAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "У вас нет подписок!",
                thirdAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Подписка выполнена успешно!",
                fourthAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Подписка отменена успешно!",
                fifthAnswer.getParameters().get("text")
            )
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "/something",
        "unknownCommand",
        ""
    })
    public void shouldReturnUnknownCommandMessage(String command) {
        Mockito.when(update.message()).thenReturn(message);
        Mockito.when(message.chat()).thenReturn(chat);
        Mockito.when(update.message().chat().id()).thenReturn(CHAT_ID);
        Mockito.when(update.message().text()).thenReturn(command);

        SendMessage answer = processor.process(update);

        Assertions.assertEquals(
            "Увы, но я не понимаю... Введите /help, чтобы узнать, какие команды есть.",
            answer.getParameters().get("text")
        );
    }
}
