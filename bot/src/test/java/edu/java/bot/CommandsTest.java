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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommandsTest {
    private static final long CHAT_ID = 10L;

    @AfterEach
    public void clearDatabase() {
        DatabaseImitation.clear();
    }

    @Test
    public void shouldReturnCorrectAnswersFromStartCommandHandler() {
        Command start = new StartCommand();

        SendMessage firstAnswer = start.handle(CHAT_ID, "/start");
        SendMessage secondAnswer = start.handle(CHAT_ID, "/start");
        SendMessage thirdAnswer = start.handle(CHAT_ID, "/start:(");
        Assertions.assertAll(
            () -> Assertions.assertEquals(
                10L,
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
        Command help = new HelpCommand(
            List.of(
                "/start - Зарегистрироваться на Link_Tracker_Bot\n",
                "/list - Ваши подписки\n",
                "/track - Подписаться\n",
                "/untrack - Отписаться\n"
            )
        );

        SendMessage firstAnswer = help.handle(CHAT_ID, "/help");
        DatabaseImitation.registerUser(CHAT_ID);
        SendMessage secondAnswer = help.handle(CHAT_ID, "/help");
        SendMessage thirdAnswer =
            help.handle(CHAT_ID, "/help (idk who can type like this but everything is possible)");
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
        Command list = new ListCommand();

        SendMessage firstAnswer = list.handle(CHAT_ID, "/list");
        DatabaseImitation.registerUser(CHAT_ID);
        SendMessage secondAnswer = list.handle(CHAT_ID, "/list");
        DatabaseImitation.addSubscriptionToUser(
            CHAT_ID,
            URI.create("https://github.com/pengrad/java-telegram-bot-api")
        );
        SendMessage thirdAnswer = list.handle(CHAT_ID, "/list");
        SendMessage fourthAnswer = list.handle(CHAT_ID, "/list something");

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
        Command track = new TrackCommand(new LinkValidatorManager(
            List.of(
                new GitHubValidator(),
                new StackOverflowValidator()
            )
        ));

        SendMessage firstAnswer = track.handle(CHAT_ID, "/track");
        DatabaseImitation.registerUser(CHAT_ID);
        SendMessage secondAnswer = track.handle(CHAT_ID, "/track https://github.com/pengrad/java-telegram-bot-api");
        SendMessage thirdAnswer = track.handle(CHAT_ID, "/track github.com/pengrad/java-telegram-bot-api");
        SendMessage fourthAnswer = track.handle(CHAT_ID, "/track incorrectURI");
        SendMessage fifthAnswer = track.handle(CHAT_ID, "/track");

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
        Command untrack = new UntrackCommand(new LinkValidatorManager(
            List.of(
                new GitHubValidator(),
                new StackOverflowValidator()
            )
        ));

        SendMessage firstAnswer = untrack.handle(CHAT_ID, "/untrack");
        DatabaseImitation.registerUser(CHAT_ID);
        SendMessage secondAnswer =
            untrack.handle(CHAT_ID, "/untrack https://github.com/pengrad/java-telegram-bot-api");
        DatabaseImitation.addSubscriptionToUser(
            CHAT_ID,
            URI.create("https://github.com/pengrad/java-telegram-bot-api")
        );
        SendMessage thirdAnswer = untrack.handle(CHAT_ID, "/untrack github.com/pengrad/java-telegram-bot-api");
        SendMessage fourthAnswer = untrack.handle(CHAT_ID, "/untrack incorrectURI");
        SendMessage fifthAnswer = untrack.handle(CHAT_ID, "/untrack");

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
