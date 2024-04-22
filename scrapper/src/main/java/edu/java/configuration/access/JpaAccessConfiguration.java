package edu.java.configuration.access;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jpa.JpaLinkService;
import edu.java.api.service.jpa.JpaTgChatService;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.scheduler.service.SchedulerService;
import edu.java.scheduler.service.jpa.JpaLinkUpdaterService;
import edu.java.scheduler.updater.LinkUpdater;
import edu.java.scheduler.updater.NotificationSender;
import edu.java.scheduler.updater.jpa.JpaGitHubLinkUpdater;
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
    LinkUpdater jpaGitHubLinkUpdater(
        JpaLinkRepository jpaLinkRepository,
        GitHubClient gitHubClient,
        NotificationSender notificationSender
    ) {
        return new JpaGitHubLinkUpdater(
            jpaLinkRepository,
            gitHubClient,
            notificationSender
        );
    }

    @Bean
    LinkUpdater jpaStackOverflowLinkUpdater(
        JpaLinkRepository jpaLinkRepository,
        StackOverflowClient stackOverflowClient,
        NotificationSender notificationSender
    ) {
        return new JpaStackOverflowLinkUpdater(
            jpaLinkRepository,
            stackOverflowClient,
            notificationSender
        );
    }

    @Bean
    SchedulerService schedulerService(
        JpaLinkRepository jpaLinkRepository,
        List<LinkUpdater> linkUpdaters
    ) {
        return new JpaLinkUpdaterService(
            jpaLinkRepository,
            linkUpdaters
        );
    }
}
