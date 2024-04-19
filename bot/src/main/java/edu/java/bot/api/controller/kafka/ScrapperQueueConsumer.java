package edu.java.bot.api.controller.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.java.bot.api.service.BotService;
import edu.java.bot.configuration.ApplicationConfig;
import edu.java.model.LinkUpdate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScrapperQueueConsumer {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApplicationConfig applicationConfig;
    private final BotService botService;
    private final KafkaTemplate<String, String> dlqKafkaTemplate;

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
            dlqKafkaTemplate.send(applicationConfig.badResponseTopicName(), data);
        }
    }
}
