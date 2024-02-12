package edu.java.bot;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.database.DatabaseImitation;
import edu.java.bot.linkvalidators.GitHubValidator;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import edu.java.bot.linkvalidators.StackOverflowValidator;
import java.net.URI;
import java.util.List;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class CommandsTest {
    @Test
    public void shouldReturnCorrectAnswersFromStartCommandHandler() {
        long randomId = 1L;
        long thirdId = 2L;
        Command start = new StartCommand();

        SendMessage firstAnswer = start.handle(randomId, "/start");
        SendMessage secondAnswer = start.handle(randomId, "/start");
        SendMessage thirdAnswer = start.handle(thirdId, "/start:(");
        Assertions.assertAll(
            () -> Assertions.assertEquals(
                1L,
                firstAnswer.getParameters().get("chat_id")
            ),
            () -> Assertions.assertEquals(
                "Вы успешно зарегистрированы!",
                firstAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Вы уже зарегистрированы!",
                secondAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Введите /start, чтобы зарегистрироваться.",
                thirdAnswer.getParameters().get("text")
            )
        );
    }

    @Test
    public void shouldReturnCorrectAnswersFromHelpCommandHandler() {
        long randomId = 3L;
        Command help = new HelpCommand(
            List.of(
                "/start - Зарегистрироваться на Link_Tracker_Bot\n",
                "/list - Ваши подписки\n",
                "/track - Подписаться\n",
                "/untrack - Отписаться\n"
            )
        );

        SendMessage firstAnswer = help.handle(randomId, "/help");
        DatabaseImitation.registerUser(randomId);
        SendMessage secondAnswer = help.handle(randomId, "/help");
        SendMessage thirdAnswer =
            help.handle(randomId, "/help (idk who can type like this but everything is possible)");
        Assertions.assertAll(
            () -> Assertions.assertEquals(
                "Вы не зарегистрированы! Введите /start",
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
                "Команда введена неправильно. Введите /help для просмотра списка команд.",
                thirdAnswer.getParameters().get("text")
            )
        );
    }

    @Test
    public void shouldReturnCorrectAnswersFromListCommandHandler() {
        long randomId = 4L;
        Command list = new ListCommand();

        SendMessage firstAnswer = list.handle(randomId, "/list");
        DatabaseImitation.registerUser(randomId);
        SendMessage secondAnswer = list.handle(randomId, "/list");
        DatabaseImitation.addSubscriptionToUser(
            randomId,
            URI.create("https://github.com/pengrad/java-telegram-bot-api")
        );
        SendMessage thirdAnswer = list.handle(randomId, "/list");
        SendMessage fourthAnswer = list.handle(randomId, "/list something");

        Assertions.assertAll(
            () -> Assertions.assertEquals(
                "Вы не зарегистрированы! Введите /start",
                firstAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "У вас нет подписок!",
                secondAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Ваши подписки:\nhttps://github.com/pengrad/java-telegram-bot-api\n",
                thirdAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Команда введена неправильно. Введите /list для просмотра ваших подписок.",
                fourthAnswer.getParameters().get("text")
            )
        );
    }

    @Test
    public void shouldReturnCorrectAnswersFromTrackCommandHandler() {
        long randomId = 5L;
        Command track = new TrackCommand(new LinkValidatorManager(
            List.of(
                new GitHubValidator(),
                new StackOverflowValidator()
            )
        ));

        SendMessage firstAnswer = track.handle(randomId, "/track");
        DatabaseImitation.registerUser(randomId);
        SendMessage secondAnswer = track.handle(randomId, "/track https://github.com/pengrad/java-telegram-bot-api");
        SendMessage thirdAnswer = track.handle(randomId, "/track github.com/pengrad/java-telegram-bot-api");
        SendMessage fourthAnswer = track.handle(randomId, "/track incorrectURI");
        SendMessage fifthAnswer = track.handle(randomId, "/track");

        Assertions.assertAll(
            () -> Assertions.assertEquals(
                "Вы не зарегистрированы! Введите /start",
                firstAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Подписка выполнена успешно!",
                secondAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Вы уже подписаны на данный ресурс!",
                thirdAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Ссылка введена неверно!",
                fourthAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Введите /track + ссылка на ресурс, чтобы подписаться на рассылку изменений.\n" +
                    "Например, /track https://...",
                fifthAnswer.getParameters().get("text")
            )
        );
    }

    @Test
    public void shouldReturnCorrectAnswersFromUntrackCommandHandler() {
        long randomId = 6L;
        Command untrack = new UntrackCommand(new LinkValidatorManager(
            List.of(
                new GitHubValidator(),
                new StackOverflowValidator()
            )
        ));

        SendMessage firstAnswer = untrack.handle(randomId, "/untrack");
        DatabaseImitation.registerUser(randomId);
        SendMessage secondAnswer =
            untrack.handle(randomId, "/untrack https://github.com/pengrad/java-telegram-bot-api");
        DatabaseImitation.addSubscriptionToUser(
            randomId,
            URI.create("https://github.com/pengrad/java-telegram-bot-api")
        );
        SendMessage thirdAnswer = untrack.handle(randomId, "/untrack github.com/pengrad/java-telegram-bot-api");
        SendMessage fourthAnswer = untrack.handle(randomId, "/untrack incorrectURI");
        SendMessage fifthAnswer = untrack.handle(randomId, "/untrack");

        Assertions.assertAll(
            () -> Assertions.assertEquals(
                "Вы не зарегистрированы! Введите /start",
                firstAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Вы не подписаны на данный ресурс!",
                secondAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Подписка отменена успешно!",
                thirdAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Ссылка введена неверно!",
                fourthAnswer.getParameters().get("text")
            ),
            () -> Assertions.assertEquals(
                "Введите /untrack + ссылка на ресурс, чтобы отписаться от рассылки изменений.\n" +
                    "Например, /untrack https://...",
                fifthAnswer.getParameters().get("text")
            )
        );
    }
}
