package edu.java.bot.database;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DatabaseImitation {
    /*
     * Данный класс создан исключительно, чтобы проверить работу скелета Телеграм-бота.
     */
    private final Map<Long, List<URI>> storage;
    private final String unknownUserMessage;

    public void registerUser(Long chatId) {
        storage.put(chatId, new ArrayList<>());
    }

    public void addSubscriptionToUser(Long chatId, URI link) throws Exception {
        if (isRegisteredUser(chatId)) {
            var subscriptions = storage.get(chatId);
            subscriptions.add(link);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public void removeSubscriptionToUser(Long chatId, URI link) throws Exception {
        if (isRegisteredUser(chatId)) {
            var subscriptions = storage.get(chatId);
            subscriptions.remove(link);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public boolean isRegisteredUser(Long chatId) {
        return storage.containsKey(chatId);
    }

    public List<URI> getUserSubscriptions(Long chatId) throws Exception {
        if (isRegisteredUser(chatId)) {
            return storage.get(chatId);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public boolean isExistSubscription(Long chatId, URI link) throws Exception {
        if (isRegisteredUser(chatId)) {
            return storage.get(chatId).contains(link);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public void clear() {
        storage.clear();
    }
}
