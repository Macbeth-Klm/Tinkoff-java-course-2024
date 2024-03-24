package edu.java.configuration;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jpa.JpaLinkService;
import edu.java.api.service.jpa.JpaTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    TgChatService tgChatService(JpaChatRepository jpaChatRepository) {
        return new JpaTgChatService(jpaChatRepository);
    }

    @Bean
    LinkService linkService(
        JpaChatRepository jpaChatRepository,
        JpaLinkRepository jpaLinkRepository
    ) {
        return new JpaLinkService(
            jpaChatRepository,
            jpaLinkRepository
        );
    }
}
