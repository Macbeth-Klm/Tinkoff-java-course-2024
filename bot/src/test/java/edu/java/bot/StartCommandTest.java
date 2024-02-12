package edu.java.bot;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.StartCommand;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class StartCommandTest {
    @Test
    public void shouldReturnSuccessfulRegistrationMessage() {
        long randomId = 1L;
        StartCommand start = new StartCommand();

        SendMessage answer = start.handle(randomId, "/start");

        Assertions.assertAll(
            () -> Assertions.assertEquals(
                1L,
                answer.getParameters().get("chat_id")
            ),
            () -> Assertions.assertEquals(
                "Вы успешно зарегистрированы!",
                answer.getParameters().get("text")
            )
        );
    }

    @Test
    public void shouldReturnAlreadyRegisteredMessage() {
        long randomId = 2L;
        StartCommand start = new StartCommand();

        var firstAnswer = start.handle(randomId, "/start");
        var secondAnswer = start.handle(randomId, "/start");

        Assertions.assertEquals(
            "Вы уже зарегистрированы!",
            secondAnswer.getParameters().get("text")
        );
    }

    @Test
    public void shouldReturnIncorrectStartCommand() {
        long randomId = 3L;
        StartCommand start = new StartCommand();

        var answer = start.handle(randomId, "/start:(");

        Assertions.assertEquals(
            "Введите /start, чтобы зарегистрироваться.",
            answer.getParameters().get("text")
        );
    }
}
