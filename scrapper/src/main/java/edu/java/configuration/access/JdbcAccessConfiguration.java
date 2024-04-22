package edu.java.configuration.access;

import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.api.service.jdbc.JdbcLinkService;
import edu.java.api.service.jdbc.JdbcTgChatService;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.scheduler.service.SchedulerService;
import edu.java.scheduler.service.jdbc.JdbcLinkUpdaterService;
import edu.java.scheduler.updater.LinkUpdater;
import edu.java.scheduler.updater.NotificationSender;
import edu.java.scheduler.updater.jdbc.JdbcGitHubLinkUpdater;
import edu.java.scheduler.updater.jdbc.JdbcStackOverflowLinkUpdater;
import java.util.List;
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

    @Bean
    LinkUpdater jdbcGitHubLinkUpdater(
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatLinkRepository jdbcChatLinkRepository,
        GitHubClient gitHubClient,
        NotificationSender notificationSender
    ) {
        return new JdbcGitHubLinkUpdater(
            jdbcLinkRepository,
            jdbcChatLinkRepository,
            gitHubClient,
            notificationSender
        );
    }

    @Bean
    LinkUpdater jdbcStackOverflowLinkUpdater(
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatLinkRepository jdbcChatLinkRepository,
        StackOverflowClient stackOverflowClient,
        NotificationSender notificationSender
    ) {
        return new JdbcStackOverflowLinkUpdater(
            jdbcLinkRepository,
            jdbcChatLinkRepository,
            stackOverflowClient,
            notificationSender
        );
    }

    @Bean
    SchedulerService schedulerService(
        JdbcLinkRepository jdbcLinkRepository,
        List<LinkUpdater> linkUpdaters
    ) {
        return new JdbcLinkUpdaterService(
            jdbcLinkRepository,
            linkUpdaters
        );
    }
}
