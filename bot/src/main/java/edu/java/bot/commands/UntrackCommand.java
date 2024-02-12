package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final LinkValidatorManager linkManager;

    @Override
    public String name() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Отписаться";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (!DatabaseImitation.isRegisteredUser(chatId)) {
            return new SendMessage(chatId, "Вы не зарегистрированы! Введите /start");
        }
        String[] commandAndUri = text.split(" ");
        if (commandAndUri.length == 2) {
            String enteredUri = commandAndUri[1];
            String scheme = "https://";
            String uri = (!enteredUri.startsWith(scheme)) ? scheme + enteredUri : enteredUri;
            if (linkManager.isValid(uri)) {
                URI link = URI.create(uri);
                String answer;
                if (DatabaseImitation.isExistSubscription(chatId, link)) {
                    DatabaseImitation.removeSubscriptionToUser(chatId, link);
                    answer = "Подписка отменена успешно!";
                } else {
                    answer = "Вы не подписаны на данный ресурс!";
                }
                return new SendMessage(chatId, answer);
            } else {
                return new SendMessage(chatId, "Ссылка введена неверно!");
            }
        }
        return new SendMessage(
            chatId,
            "Введите /untrack + ссылка на ресурс, чтобы отписаться от рассылки изменений.\n"
                + "Например, /untrack https://..."
        );
    }
}
