package edu.java.bot.api.controller.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.service.BotService;
import edu.java.model.LinkUpdate;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScrapperQueueConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BotService botService;
    private final KafkaTemplate<String, String> dlqKafkaTemplate;
    @Value(value = "${app.badResponseTopicName}")
    private String badResponseTopicName;

    public ScrapperQueueConsumer(
        @Qualifier("regularBotService") BotService botService,
        KafkaTemplate<String, String> dlqKafkaTemplate
    ) {
        this.botService = botService;
        this.dlqKafkaTemplate = dlqKafkaTemplate;
    }

    @PostConstruct
    private void initObjectMapper() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
    }

    @KafkaListener(topics = "${app.topicName}")
    public void listen(String data) {
        try {
            LinkUpdate update = objectMapper.readValue(data, LinkUpdate.class);
            botService.postUpdate(update);
        } catch (Exception e) {
            dlqKafkaTemplate.send(badResponseTopicName, data);
        }
    }
}
