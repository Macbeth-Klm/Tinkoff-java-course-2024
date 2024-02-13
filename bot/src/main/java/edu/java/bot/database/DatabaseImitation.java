package edu.java.bot.database;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DatabaseImitation {
    /*
     * Данный класс создан исключительно, чтобы проверить работу скелета Телеграм-бота.
     */
    private static final Map<Long, List<URI>> STORAGE = new HashMap<>();

    private DatabaseImitation() {
    }

    public static void registerUser(Long chatId) {
        STORAGE.put(chatId, new ArrayList<>());
    }

    public static void addSubscriptionToUser(Long chatId, URI link) {
        var subscriptions = STORAGE.get(chatId);
        subscriptions.add(link);
    }

    public static void removeSubscriptionToUser(Long chatId, URI link) {
        var subscriptions = STORAGE.get(chatId);
        subscriptions.remove(link);
    }

    public static boolean isRegisteredUser(Long chatId) {
        return STORAGE.containsKey(chatId);
    }

    public static List<URI> getUserSubscriptions(Long chatId) {
        return STORAGE.get(chatId);
    }

    public static boolean isExistSubscription(Long chatId, URI link) {
        return STORAGE.get(chatId).contains(link);
    }

    public static void clear() {
        STORAGE.clear();
    }
}
