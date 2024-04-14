package edu.java.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.processor.UserMessageProcessor;
import io.micrometer.core.instrument.Counter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Bot implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final UserMessageProcessor processor;

    private final Counter counter;

    public Bot(TelegramBot telegramBot, UserMessageProcessor processor, Counter counter) {
        this.telegramBot = telegramBot;
        this.processor = processor;
        this.counter = counter;
        telegramBot.execute(createCommandMenu());
        telegramBot.setUpdatesListener(this);
    }

    private SetMyCommands createCommandMenu() {
        return new SetMyCommands(
            processor.commands().stream().map(command -> new BotCommand(
                command.name(),
                command.description()
            )).toArray(BotCommand[]::new)
        );
    }

    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            SendMessage message = processor.process(update);
            telegramBot.execute(message);
            counter.increment();
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
