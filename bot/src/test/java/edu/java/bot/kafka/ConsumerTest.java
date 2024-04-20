package edu.java.bot.kafka;

import edu.java.bot.api.controller.kafka.ScrapperQueueConsumer;
import edu.java.bot.api.service.BotService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Slf4j
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Import(ConsumerTest.TestKafkaConfig.class)
public class ConsumerTest extends KafkaIntegrationTest {
    @Autowired
    ScrapperQueueConsumer testConsumer;
    @Value(value = "${app.topicName}")
    private String topicName;
    @Value(value = "${app.badResponseTopicName}")
    private String badResponseTopicName;

    @Test
    @Order(1)
    public void shouldGetCorrectMessage() throws InterruptedException {
        String data = """
            {
              "id": 1,
              "url": "https://github.com/Macbeth-Klm/Tinkoff-java-course-2023",
              "description": "kafka message",
              "tgChatIds":
                [
                    1
                ]
            }
            """;
        KAFKA_TEMPLATE.send(topicName, data);
        Thread.sleep(1000);

        ConsumerRecords<String, String> polled;
        try (Consumer<String, String> consumer = CONSUMER_FACTORY.createConsumer()) {
            consumer.subscribe(List.of(badResponseTopicName));
            polled = consumer.poll(Duration.ofSeconds(15));
        }

        List<ConsumerRecord<String, String>> polledList = new ArrayList<>();
        polled.iterator().forEachRemaining(polledList::add);

        Assertions.assertTrue(polledList.isEmpty());
    }

    @Test
    @Order(2)
    public void shouldAddMessageIntoDLQTopic() throws InterruptedException {
        String data = "invalid kafka message";
        KAFKA_TEMPLATE.send(topicName, data);
        Thread.sleep(1000);

        ConsumerRecords<String, String> polled;
        try (Consumer<String, String> consumer = CONSUMER_FACTORY.createConsumer()) {
            consumer.subscribe(List.of(badResponseTopicName));
            polled = consumer.poll(Duration.ofSeconds(15));
        }

        List<ConsumerRecord<String, String>> polledList = new ArrayList<>();
        polled.iterator().forEachRemaining(polledList::add);

        Assertions.assertAll(
            () -> Assertions.assertEquals(1, polledList.size()),
            () -> Assertions.assertEquals("invalid kafka message", polledList.getFirst().value())
        );
    }

    @TestConfiguration
    static class TestKafkaConfig {
        @Bean
        BotService mockedService() {
            return (req) -> log.info("BotService::postUpdates");
        }

        @Bean
        ScrapperQueueConsumer testConsumer(@Qualifier("mockedService") BotService mockedService) {
            return new ScrapperQueueConsumer(
                mockedService,
                KAFKA_TEMPLATE
            );
        }
    }
}
