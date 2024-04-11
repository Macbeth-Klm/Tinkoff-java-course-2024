package edu.java.bot.api.controller.kafka;

import edu.java.bot.api.service.BotService;
import edu.java.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapperQueueConsumer {
    private final BotService botService;

    @KafkaListener(topics = "${app.kafka.topicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listen(LinkUpdate update) {
        botService.postUpdate(update);
    }
}
