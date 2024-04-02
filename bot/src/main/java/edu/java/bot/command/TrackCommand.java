package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.exception.ApiException;
import edu.java.model.AddLinkRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TrackCommand implements Command {
    private final ScrapperClient scrapperClient;

    @Override
    public String name() {
        return "/track";
    }

    @Override
    public String description() {
        return "Подписаться";
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
                scrapperClient.addLink(chatId, new AddLinkRequest(link));
                return new SendMessage(chatId, "Подписка выполнена успешно!");
            } catch (ApiException e) {
                return new SendMessage(chatId, e.getDescription());
            }
        }
        return new SendMessage(
            chatId,
            "Введите /track + ссылка на ресурс, чтобы подписаться на рассылку изменений.\n"
                + "Например, /track https://..."
        );
    }
}
