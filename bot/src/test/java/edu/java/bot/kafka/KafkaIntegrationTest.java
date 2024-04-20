package edu.java.bot.kafka;

import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class KafkaIntegrationTest {
    public static KafkaContainer KAFKA;
    public static ConsumerFactory<String, String> CONSUMER_FACTORY;
    public static KafkaTemplate<String, String> KAFKA_TEMPLATE;

    static {
        KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        KAFKA.start();
        CONSUMER_FACTORY = createConsumerFactory();
        KAFKA_TEMPLATE = createKafkaTemplate();
    }

    public static ConsumerFactory<String, String> createConsumerFactory() {
        Map<String, Object> configs = Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers(),

            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,

            ConsumerConfig.GROUP_ID_CONFIG, "bot-messages-test",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
        );
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    public static KafkaTemplate<String, String> createKafkaTemplate() {
        Map<String, Object> configs = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
        );
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    @DynamicPropertySource
    static void kafkaPropertiesSource(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}

