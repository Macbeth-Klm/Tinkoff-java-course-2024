package edu.java.configuration.kafka;

import edu.java.configuration.ApplicationConfig;
import edu.java.model.LinkUpdate;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {
    private final ApplicationConfig applicationConfig;

    @Bean
    public KafkaTemplate<String, LinkUpdate> kafkaTemplate(
        ProducerFactory<String, LinkUpdate> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, LinkUpdate> producerFactory() {
        Map<String, Object> configs = Map.of(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationConfig.kafka().bootstrapServers(),
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, applicationConfig.kafka().producer().keySerializer(),
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, applicationConfig.kafka().producer().valueSerializer()

        );

        return new DefaultKafkaProducerFactory<>(configs);
    }
}
