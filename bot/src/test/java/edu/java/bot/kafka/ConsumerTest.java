//package edu.java.bot.kafka;
//
//import edu.java.bot.api.controller.kafka.ScrapperQueueConsumer;
//import edu.java.bot.api.service.BotService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Import;
//
//@Slf4j
//@SpringBootTest
//@Import({ConsumerTest.TestConfig.class, ScrapperQueueConsumer.class})
//public class ConsumerTest extends KafkaIntegrationTest {
//    @Autowired
//    ScrapperQueueConsumer testConsumer;
//    @Value(value = "${app.topicName}")
//    private String topicName;
//
//    @Test
//    public void shouldGetCorrectMessage() throws InterruptedException {
//        var consumerMock = Mockito.spy(testConsumer);
//        String data = """
//            {
//              "id": 1,
//              "url": "https://github.com/Macbeth-Klm/Tinkoff-java-course-2023",
//              "description": "kafka message",
//              "tgChatIds":
//                [
//                    1
//                ]
//            }
//            """;
//        KAFKA_TEMPLATE.send(topicName, data);
//        Thread.sleep(2000);
//
//        Mockito.verify(consumerMock, Mockito.times(1)).listen(data);
//    }
//
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        BotService mocked() {
//            return (req) -> log.info("BotService::postUpdates");
//        }
//
//        @Bean
//        ScrapperQueueConsumer testConsumer(@Qualifier("mocked") BotService mocked) {
//            return new ScrapperQueueConsumer(
//                mocked,
//                KAFKA_TEMPLATE
//            );
//        }
//    }
//}
