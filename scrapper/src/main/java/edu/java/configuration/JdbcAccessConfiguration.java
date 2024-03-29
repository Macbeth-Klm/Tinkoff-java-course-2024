package edu.java.configuration;

import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jdbc.JdbcLinkService;
import edu.java.api.service.jdbc.JdbcTgChatService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    TgChatService tgChatService(JdbcChatRepository jdbcChatRepository) {
        return new JdbcTgChatService(jdbcChatRepository);
    }

    @Bean
    LinkService linkService(
        JdbcChatRepository jdbcChatRepository,
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatLinkRepository jdbcChatLinkRepository
    ) {
        return new JdbcLinkService(
            jdbcChatRepository,
            jdbcLinkRepository,
            jdbcChatLinkRepository
        );
    }
}
