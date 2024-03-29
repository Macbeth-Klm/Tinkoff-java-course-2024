package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final LinkValidatorManager linkManager;
    private final DatabaseImitation database;

    @Override
    public String name() {
        return "/track";
    }

    @Override
    public String description() {
        return "Подписаться";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        String[] commandAndUri = text.split(" ");
        if (commandAndUri.length == 2) {
            String enteredUri = commandAndUri[1];
            String scheme = "https://";
            String uri = (!enteredUri.startsWith(scheme)) ? scheme + enteredUri : enteredUri;
            if (linkManager.isValid(uri)) {
                URI link = URI.create(uri);
                String answer;
                try {
                    if (!database.isExistSubscription(chatId, link)) {
                        database.addSubscriptionToUser(chatId, link);
                        answer = "Подписка выполнена успешно!";
                    } else {
                        answer = "Вы уже подписаны на данный ресурс!";
                    }
                    return new SendMessage(chatId, answer);
                } catch (Exception e) {
                    return new SendMessage(chatId, "Вы не зарегистрированы! Введите /start");
                }
            } else {
                return new SendMessage(chatId, "Ссылка введена неверно!");
            }
        }
        return new SendMessage(
            chatId,
            "Введите /track + ссылка на ресурс, чтобы подписаться на рассылку изменений.\n"
                + "Например, /track https://..."
        );
    }
}
