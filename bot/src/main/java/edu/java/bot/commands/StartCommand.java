package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.exceptions.ApiException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartCommand implements Command {
    private final ScrapperClient scrapperClient;

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
            try {
                scrapperClient.registerChat(chatId);
            } catch (ApiException e) {
                return new SendMessage(chatId, e.getDescription());
            }
            return new SendMessage(chatId, "Вы успешно зарегистрированы!");
        }
        return new SendMessage(chatId, "Введите /start, чтобы зарегистрироваться.");
    }
}
