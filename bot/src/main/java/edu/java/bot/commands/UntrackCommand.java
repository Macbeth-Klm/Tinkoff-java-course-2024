package edu.java.bot.commands;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import edu.java.exceptions.ApiException;
import edu.java.models.RemoveLinkRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UntrackCommand implements Command {
    private final LinkValidatorManager linkManager;
    private final ScrapperClient scrapperClient;

    @Override
    public String name() {
        return "/untrack";
    }

    @Override
    public String description() {
        return "Отписаться";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        String[] commandAndUri = text.split(" ");
        if (commandAndUri.length == 2) {
            String enteredUri = commandAndUri[1];
            String scheme = "https://";
            String uri = (!enteredUri.startsWith(scheme)) ? scheme + enteredUri : enteredUri;
            if (linkManager.isValid(uri)) {
                URI link = URI.create(uri);
                try {
                    scrapperClient.deleteLink(chatId, new RemoveLinkRequest(link));
                    return new SendMessage(chatId, "Подписка отменена успешно!");
                } catch (ApiException e) {
                    return new SendMessage(chatId, e.getDescription());
                }
            } else {
                return new SendMessage(chatId, "Ссылка введена неверно!");
            }
        }
        return new SendMessage(
            chatId,
            "Введите /untrack + ссылка на ресурс, чтобы отписаться от рассылки изменений.\n"
                + "Например, /untrack https://..."
        );
    }
}
