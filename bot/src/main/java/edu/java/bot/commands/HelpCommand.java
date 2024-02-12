package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class HelpCommand implements Command {
    private final List<Command> commands;

    public HelpCommand(
        Command startCommand,
        Command listCommand,
        Command trackCommand,
        Command untrackCommand
    ) {
        commands = List.of(
            startCommand,
            listCommand,
            trackCommand,
            untrackCommand
        );
    }

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
        if (!DatabaseImitation.isRegisteredUser(chatId)) {
            return new SendMessage(chatId, "Вы не зарегистрированы! Введите /start");
        }
        if (text.equals(this.name())) {
            StringBuilder sb = new StringBuilder("Список команд:\n");
            for (var command : commands) {
                sb.append(command.name()).append(" - ").append(command.description()).append("\n");
            }
            return new SendMessage(chatId, sb.toString());
        }
        return new SendMessage(chatId, "Команда введена неправильно. Введите /help для просмотра списка команд.");
    }
}
