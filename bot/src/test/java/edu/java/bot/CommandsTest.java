//package edu.java.bot;
//
//import com.pengrad.telegrambot.request.SendMessage;
//import edu.java.bot.commands.Command;
//import edu.java.bot.database.DatabaseImitation;
//import java.net.URI;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//public class CommandsTest {
//    private static final long CHAT_ID = 10L;
//    @Autowired
//    Command helpCommand;
//    @Autowired
//    Command listCommand;
//    @Autowired
//    Command startCommand;
//    @Autowired
//    Command trackCommand;
//    @Autowired
//    Command untrackCommand;
//    @Autowired
//    DatabaseImitation database;
//
//    @AfterEach
//    public void clearDatabase() {
//        database.clear();
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromStartCommandHandler() {
//        SendMessage firstAnswer = startCommand.handle(CHAT_ID, "/start");
//        SendMessage secondAnswer = startCommand.handle(CHAT_ID, "/start");
//        SendMessage thirdAnswer = startCommand.handle(CHAT_ID, "/start:(");
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                10L,
//                firstAnswer.getParameters().get("chat_id")
//            ),
//            () -> Assertions.assertEquals(
//                "Вы успешно зарегистрированы!",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Вы уже зарегистрированы!",
//                secondAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Введите /start, чтобы зарегистрироваться.",
//                thirdAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromHelpCommandHandler() {
//        SendMessage firstAnswer = helpCommand.handle(CHAT_ID, "/help");
//        SendMessage secondAnswer =
//            helpCommand.handle(CHAT_ID, "/help (idk who can type like this but everything is possible)");
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                """
//                    Список команд:
//                    /start - Зарегистрироваться на Link_Tracker_Bot
//                    /list - Ваши подписки
//                    /track - Подписаться
//                    /untrack - Отписаться
//                    """,
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Команда введена неправильно. Введите /help для просмотра списка команд.",
//                secondAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromListCommandHandler() throws Exception {
//        SendMessage firstAnswer = listCommand.handle(CHAT_ID, "/list");
//        database.registerUser(CHAT_ID);
//        SendMessage secondAnswer = listCommand.handle(CHAT_ID, "/list");
//        database.addSubscriptionToUser(
//            CHAT_ID,
//            URI.create("https://github.com/pengrad/java-telegram-bot-api")
//        );
//        SendMessage thirdAnswer = listCommand.handle(CHAT_ID, "/list");
//        SendMessage fourthAnswer = listCommand.handle(CHAT_ID, "/list something");
//
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                "Вы не зарегистрированы! Введите /start",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "У вас нет подписок!",
//                secondAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Ваши подписки:\nhttps://github.com/pengrad/java-telegram-bot-api\n",
//                thirdAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Команда введена неправильно. Введите /list для просмотра ваших подписок.",
//                fourthAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromTrackCommandHandler() {
//        SendMessage firstAnswer =
//            trackCommand.handle(CHAT_ID, "/track https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
//        database.registerUser(CHAT_ID);
//        SendMessage secondAnswer =
//            trackCommand.handle(CHAT_ID, "/track https://github.com/pengrad/java-telegram-bot-api");
//        SendMessage thirdAnswer = trackCommand.handle(CHAT_ID, "/track github.com/pengrad/java-telegram-bot-api");
//        SendMessage fourthAnswer = trackCommand.handle(CHAT_ID, "/track incorrectURI");
//        SendMessage fifthAnswer = trackCommand.handle(CHAT_ID, "/track");
//
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                "Вы не зарегистрированы! Введите /start",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Подписка выполнена успешно!",
//                secondAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Вы уже подписаны на данный ресурс!",
//                thirdAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Ссылка введена неверно!",
//                fourthAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Введите /track + ссылка на ресурс, чтобы подписаться на рассылку изменений.\n" +
//                    "Например, /track https://...",
//                fifthAnswer.getParameters().get("text")
//            )
//        );
//    }
//
//    @Test
//    public void shouldReturnCorrectAnswersFromUntrackCommandHandler() throws Exception {
//        SendMessage firstAnswer =
//            untrackCommand.handle(CHAT_ID, "/untrack https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
//        database.registerUser(CHAT_ID);
//        SendMessage secondAnswer =
//            untrackCommand.handle(CHAT_ID, "/untrack https://github.com/pengrad/java-telegram-bot-api");
//        database.addSubscriptionToUser(
//            CHAT_ID,
//            URI.create("https://github.com/pengrad/java-telegram-bot-api")
//        );
//        SendMessage thirdAnswer = untrackCommand.handle(CHAT_ID, "/untrack github.com/pengrad/java-telegram-bot-api");
//        SendMessage fourthAnswer = untrackCommand.handle(CHAT_ID, "/untrack incorrectURI");
//        SendMessage fifthAnswer = untrackCommand.handle(CHAT_ID, "/untrack");
//
//        Assertions.assertAll(
//            () -> Assertions.assertEquals(
//                "Вы не зарегистрированы! Введите /start",
//                firstAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Вы не подписаны на данный ресурс!",
//                secondAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Подписка отменена успешно!",
//                thirdAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Ссылка введена неверно!",
//                fourthAnswer.getParameters().get("text")
//            ),
//            () -> Assertions.assertEquals(
//                "Введите /untrack + ссылка на ресурс, чтобы отписаться от рассылки изменений.\n" +
//                    "Например, /untrack https://...",
//                fifthAnswer.getParameters().get("text")
//            )
//        );
//    }
//}
