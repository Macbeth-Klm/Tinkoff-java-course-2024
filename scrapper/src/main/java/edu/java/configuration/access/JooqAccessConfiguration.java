package edu.java.configuration.access;

import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jooq.JooqLinkService;
import edu.java.api.service.jooq.JooqTgChatService;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.scheduler.service.SchedulerService;
import edu.java.scheduler.service.jooq.JooqLinkUpdaterService;
import edu.java.scheduler.updater.LinkUpdater;
import edu.java.scheduler.updater.NotificationSender;
import edu.java.scheduler.updater.jooq.JooqGitHubLinkUpdater;
import edu.java.scheduler.updater.jooq.JooqStackOverflowLinkUpdater;
import java.util.List;
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

    @Bean
    LinkUpdater jooqGitHubLinkUpdater(
        JooqLinkRepository jooqLinkRepository,
        JooqChatLinkRepository jooqChatLinkRepository,
        GitHubClient gitHubClient,
        NotificationSender notificationSender
    ) {
        return new JooqGitHubLinkUpdater(
            jooqLinkRepository,
            jooqChatLinkRepository,
            gitHubClient,
            notificationSender
        );
    }

    @Bean
    LinkUpdater jooqStackOverflowLinkUpdater(
        JooqLinkRepository jooqLinkRepository,
        JooqChatLinkRepository jooqChatLinkRepository,
        StackOverflowClient stackOverflowClient,
        NotificationSender notificationSender
    ) {
        return new JooqStackOverflowLinkUpdater(
            jooqLinkRepository,
            jooqChatLinkRepository,
            stackOverflowClient,
            notificationSender
        );
    }

    @Bean
    SchedulerService schedulerService(
        JooqLinkRepository jooqLinkRepository,
        List<LinkUpdater> linkUpdaters
    ) {
        return new JooqLinkUpdaterService(
            jooqLinkRepository,
            linkUpdaters
        );
    }
}
