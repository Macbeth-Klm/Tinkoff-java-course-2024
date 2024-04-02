package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HelpCommand implements Command {
    private final List<String> commands;

    @Override
    public String name() {
        return "/help";
    }

    @Override
    public String description() {
        return "Список команд";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.equals(this.name())) {
            StringBuilder sb = new StringBuilder("Список команд:\n");
            for (var command : commands) {
                sb.append(command);
            }
            return new SendMessage(chatId, sb.toString());
        }
        return new SendMessage(chatId, "Команда введена неправильно. Введите /help для просмотра списка команд.");
    }
}
