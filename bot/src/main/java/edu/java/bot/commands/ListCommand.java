package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ListCommand implements Command {
    @Override
    public String name() {
        return "/list";
    }

    @Override
    public String description() {
        return "Ваши подписки";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (!DatabaseImitation.isRegisteredUser(chatId)) {
            return new SendMessage(chatId, "Вы не зарегистрированы! Введите /start");
        }
        if (text.equals(this.name())) {
            if (DatabaseImitation.isRegisteredUser(chatId)) {
                List<URI> links = DatabaseImitation.getUserSubscriptions(chatId);
                if (links.isEmpty()) {
                    return new SendMessage(chatId, "У вас нет подписок!");
                }
                StringBuilder sb = new StringBuilder("Ваши подписки:\n");
                for (var link : links) {
                    sb.append(link.toString()).append("\n");
                }
                return new SendMessage(chatId, sb.toString());
            }
        }
        return new SendMessage(chatId, "Команда введена неправильно. Введите /list для просмотра ваших подписок.");
    }
}
