package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListCommand implements Command {
    private final DatabaseImitation database;

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
        if (text.equals(this.name())) {
            try {
                List<URI> links = database.getUserSubscriptions(chatId);
                if (links.isEmpty()) {
                    return new SendMessage(chatId, "У вас нет подписок!");
                }
                StringBuilder sb = new StringBuilder("Ваши подписки:\n");
                for (var link : links) {
                    sb.append(link.toString()).append("\n");
                }
                return new SendMessage(chatId, sb.toString());
            } catch (Exception e) {
                return new SendMessage(chatId, "Вы не зарегистрированы! Введите /start");
            }
        }
        return new SendMessage(chatId, "Команда введена неправильно. Введите /list для просмотра ваших подписок.");
    }
}
