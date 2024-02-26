package edu.java.bot.database;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
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
        storage.put(chatId, Collections.synchronizedList(new ArrayList<>()));
    }

    public void addSubscriptionToUser(Long chatId, URI link) throws Exception {
        var subscriptions = storage.get(chatId);
        if (subscriptions != null) {
            subscriptions.add(link);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public void removeSubscriptionToUser(Long chatId, URI link) throws Exception {
        var subscriptions = storage.get(chatId);
        if (subscriptions != null) {
            subscriptions.remove(link);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public boolean isRegisteredUser(Long chatId) {
        return storage.containsKey(chatId);
    }

    public List<URI> getUserSubscriptions(Long chatId) throws Exception {
        var subscriptions = storage.get(chatId);
        if (subscriptions != null) {
            return subscriptions;
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public boolean isExistSubscription(Long chatId, URI link) throws Exception {
        var subscriptions = storage.get(chatId);
        if (subscriptions != null) {
            return subscriptions.contains(link);
        } else {
            throw new Exception(unknownUserMessage);
        }
    }

    public void clear() {
        storage.clear();
    }
}
