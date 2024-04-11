package edu.java.scheduler.updater;

import edu.java.client.BotClient.BotClient;
import edu.java.client.ScrapperQueueProducer;
import edu.java.configuration.ApplicationConfig;
import edu.java.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationSender {
    private final ApplicationConfig applicationConfig;
    private final BotClient botClient;
    private final ScrapperQueueProducer scrapperQueueProducer;
    /*
    Не совсем понимаю задумку в виде написания отдельного сервиса.
    Как-будто бы тут просится просто создать конфиги по типу AccessType в дз6, где мы
    в зависимости от того, что указано в .yml будем инициализировать или HTTP-клиент, или
    же Kafk'у. Я сделал, как требовалось в задании, а тут просто решил описать свои мысли,
    чтобы получить какой-то комментарий насчёт своих мыслей.
     */

    public void sendUpdate(LinkUpdate linkUpdate) {
        if (applicationConfig.useQueue()) {
            scrapperQueueProducer.send(linkUpdate);
        } else {
            botClient.postUpdates(linkUpdate);
        }
    }
}
