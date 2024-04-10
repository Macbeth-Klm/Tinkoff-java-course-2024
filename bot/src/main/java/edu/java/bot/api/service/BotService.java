package edu.java.bot.api.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import edu.java.exception.BadRequestException;
import edu.java.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
@RequiredArgsConstructor
public class BotService {
    private final TelegramBot telegramBot;

    public void postUpdate(LinkUpdate req, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(
                "Invalid HTTP-request parameters",
                "Некорректные параметры запроса"
            );
        }
        String message = req.description();
        for (Long tgChatId : req.tgChatIds()) {
            telegramBot.execute(new SendMessage(tgChatId, message));
        }
    }
}
