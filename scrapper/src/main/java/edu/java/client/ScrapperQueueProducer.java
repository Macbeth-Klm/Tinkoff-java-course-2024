package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import edu.java.model.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScrapperQueueProducer {
    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final ApplicationConfig applicationConfig;

    public void send(LinkUpdate update) {
        kafkaTemplate.send(applicationConfig.kafka().topicName(), update);
    }
}
