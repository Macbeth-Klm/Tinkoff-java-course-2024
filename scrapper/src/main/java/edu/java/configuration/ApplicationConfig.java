package edu.java.configuration;

import edu.java.configuration.access.AccessType;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull
    Scheduler scheduler,
    @NotNull
    AccessType databaseAccessType,
    @NotNull
    Boolean useQueue,
    @NotNull
    Kafka kafka) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

    public record Kafka(
        String bootstrapServers,
        Producer producer,
        String topicName
    ) {
        public record Producer(String keySerializer, String valueSerializer) {
        }
    }
}
