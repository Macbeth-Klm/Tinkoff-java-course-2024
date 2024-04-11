package edu.java.bot.api.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotService {
    private final TelegramBot telegramBot;

    public void postUpdate(LinkUpdate req) {
        String message = req.description();
        for (Long tgChatId : req.tgChatIds()) {
            telegramBot.execute(new SendMessage(tgChatId, message));
        }
    }
}
