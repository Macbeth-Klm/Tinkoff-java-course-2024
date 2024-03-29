package edu.java.configuration;

import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jooq.JooqLinkService;
import edu.java.api.service.jooq.JooqTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfiguration {
    @Bean
    TgChatService tgChatService(JooqChatRepository jooqChatRepository) {
        return new JooqTgChatService(jooqChatRepository);
    }

    @Bean
    LinkService linkService(
        JooqChatRepository jooqChatRepository,
        JooqLinkRepository jooqLinkRepository,
        JooqChatLinkRepository jooqChatLinkRepository
    ) {
        return new JooqLinkService(
            jooqChatRepository,
            jooqLinkRepository,
            jooqChatLinkRepository
        );
    }
}
