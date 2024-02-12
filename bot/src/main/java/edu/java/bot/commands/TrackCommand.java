package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements Command {
    private final LinkValidatorManager linkManager;

    public TrackCommand(LinkValidatorManager linkManager) {
        this.linkManager = linkManager;
    }

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
                if (!DatabaseImitation.isExistSubscription(chatId, link)) {
                    DatabaseImitation.addSubscriptionToUser(chatId, link);
                    answer = "Подписка выполнена успешно!";
                } else {
                    answer = "Вы уже подписаны на данный ресурс!";
                }
                return new SendMessage(chatId, answer);
            } else {
                return new SendMessage(chatId, "Ссылка введена неверно!");
            }
        }
        return new SendMessage(
            chatId,
            "Введите /track + ссылка на ресурс, чтобы подписаться на рассылку изменений.\n" +
                "Например, /track https://..."
        );
    }
}
