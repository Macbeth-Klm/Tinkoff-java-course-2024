package edu.java.bot.processor;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.command.Command;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Processor implements UserMessageProcessor {
    private static final String UNKNOWN_COMMAND =
        "Увы, но я не понимаю... Введите /help, чтобы узнать, какие команды есть.";
    private final List<Command> commands;

    public Processor(List<Command> commands) {
        this.commands = Collections.unmodifiableList(commands);
    }

    @Override
    public List<Command> commands() {
        return commands;
    }

    @Override
    public SendMessage process(Update update) {
        long chatId = update.message().chat().id();
        String text = update.message().text();
        for (var command : commands) {
            if (text.startsWith(command.name())) {
                return command.handle(chatId, text);
            }
        }
        return new SendMessage(chatId, UNKNOWN_COMMAND);
    }
}
