package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.database.DatabaseImitation;

public class StartCommand implements Command {
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
            if (DatabaseImitation.isRegisteredUser(chatId)) {
                return new SendMessage(chatId, "Вы уже зарегистрированы!");
            }
            DatabaseImitation.registerUser(chatId);
            return new SendMessage(chatId, "Вы успешно зарегистрированы!");
        }
        return new SendMessage(chatId, "Введите /start, чтобы зарегистрироваться.");
    }
}
