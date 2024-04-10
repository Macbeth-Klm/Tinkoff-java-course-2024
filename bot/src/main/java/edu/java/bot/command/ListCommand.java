package edu.java.bot.command;

import com.pengrad.telegrambot.request.SendMessage;
import edu.java.bot.client.ScrapperClient;
import edu.java.exception.ApiException;
import edu.java.model.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListCommand implements Command {
    private final ScrapperClient scrapperClient;

    @Override
    public String name() {
        return "/list";
    }

    @Override
    public String description() {
        return "Ваши подписки";
    }

    @Override
    public SendMessage handle(long chatId, String text) {
        if (text.equals(this.name())) {
            try {
                List<URI> links = scrapperClient.getLinks(chatId).get().links().stream()
                    .map(LinkResponse::url).toList();
                if (links.isEmpty()) {
                    return new SendMessage(chatId, "У вас нет подписок!");
                }
                StringBuilder sb = new StringBuilder("Ваши подписки:\n");
                for (var link : links) {
                    sb.append(link.toString()).append("\n");
                }
                return new SendMessage(chatId, sb.toString());
            } catch (ApiException e) {
                return new SendMessage(chatId, e.getDescription());
            }
        }
        return new SendMessage(chatId, "Команда введена неправильно. Введите /list для просмотра ваших подписок.");
    }
}
