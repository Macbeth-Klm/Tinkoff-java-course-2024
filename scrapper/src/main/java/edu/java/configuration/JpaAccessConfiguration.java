package edu.java.configuration;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jpa.JpaLinkService;
import edu.java.api.service.jpa.JpaTgChatService;
import edu.java.client.BotClient.BotClient;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.scheduler.service.LinkUpdaterService;
import edu.java.scheduler.service.jpa.JpaLinkUpdaterService;
import edu.java.scheduler.updater.jpa.JpaGitHubLinkUpdater;
import edu.java.scheduler.updater.jpa.JpaLinkUpdater;
import edu.java.scheduler.updater.jpa.JpaStackOverflowLinkUpdater;
import java.util.List;
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

    @Bean
    JpaLinkUpdater jpaGitHubLinkUpdater(
        JpaLinkRepository jpaLinkRepository,
        GitHubClient gitHubClient,
        BotClient botClient
    ) {
        return new JpaGitHubLinkUpdater(
            jpaLinkRepository,
            gitHubClient,
            botClient
        );
    }

    @Bean
    JpaLinkUpdater jpaStackOverflowLinkUpdater(
        JpaLinkRepository jpaLinkRepository,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        return new JpaStackOverflowLinkUpdater(
            jpaLinkRepository,
            stackOverflowClient,
            botClient
        );
    }

    @Bean
    LinkUpdaterService linkUpdaterService(
        JpaLinkRepository jpaLinkRepository,
        List<JpaLinkUpdater> linkUpdaters
    ) {
        return new JpaLinkUpdaterService(
            jpaLinkRepository,
            linkUpdaters
        );
    }
}
