package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartCommand implements Command {
    private final DatabaseImitation database;

    @Override
    public String name() {
        return "/start";
    }

    @Override
    public String description() {
        return "Зарегистрироваться на Link_Tracker_Bot";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.equals(this.name())) {
            if (database.isRegisteredUser(chatId)) {
                return new SendMessage(chatId, "Вы уже зарегистрированы!");
            }
            database.registerUser(chatId);
            return new SendMessage(chatId, "Вы успешно зарегистрированы!");
        }
        return new SendMessage(chatId, "Введите /start, чтобы зарегистрироваться.");
    }
}
