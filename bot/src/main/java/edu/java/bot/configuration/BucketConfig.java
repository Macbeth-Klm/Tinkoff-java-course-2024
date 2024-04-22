package edu.java.bot.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfig {
    @Value(value = "${bucket.queryCount}")
    private int queryCount;

    @Bean
    public Bucket bucket() {
        Bandwidth limit = Bandwidth
            .builder()
            .capacity(queryCount)
            .refillIntervally(queryCount, Duration.ofMinutes(1))
            .build();

        return Bucket.builder().addLimit(limit).build();
    }
}
