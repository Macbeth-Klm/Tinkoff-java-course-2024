package edu.java.bot.api.controller.kafka;

import edu.java.bot.api.service.BotService;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapperQueueConsumer {
    private final ApplicationConfig applicationConfig;
    private final BotService botService;
    private final KafkaTemplate<String, LinkUpdate> dlqKafkaTemplate;

    @KafkaListener(topics = "${app.kafka.topicName}", groupId = "${app.kafka.consumer.group-id}")
    public void listen(LinkUpdate update) {
        try {
            botService.postUpdate(update);
        } catch (Exception e) {
            dlqKafkaTemplate.send(applicationConfig.kafka().badResponseTopicName(), update);
        }
    }
}
