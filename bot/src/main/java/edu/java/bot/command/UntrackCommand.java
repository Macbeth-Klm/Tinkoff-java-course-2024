package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.exception.ApiException;
import edu.java.model.RemoveLinkRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UntrackCommand implements Command {
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
            URI link = URI.create(uri);
            try {
                scrapperClient.retryDeleteLink(chatId, new RemoveLinkRequest(link));
                return new SendMessage(chatId, "Подписка отменена успешно!");
            } catch (ApiException e) {
                return new SendMessage(chatId, e.getDescription());
            }
        }
        return new SendMessage(
            chatId,
            "Введите /untrack + ссылка на ресурс, чтобы отписаться от рассылки изменений.\n"
                + "Например, /untrack https://..."
        );
    }
}
