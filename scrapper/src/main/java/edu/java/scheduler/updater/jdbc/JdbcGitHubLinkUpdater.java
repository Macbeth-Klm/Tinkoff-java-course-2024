package edu.java.scheduler.updater.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.model.domain.GeneralLink;
import edu.java.model.domain.dto.ChatLinkDto;
import edu.java.response.GitHubResponse;
import edu.java.response.ResourceResponse;
import edu.java.scheduler.updater.LinkUpdater;
import edu.java.scheduler.updater.NotificationSender;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;

public class JdbcGitHubLinkUpdater extends LinkUpdater {
    @Getter
    private final String host = "github.com";
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final GitHubClient gitHubClient;

    public JdbcGitHubLinkUpdater(
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatLinkRepository jdbcChatLinkRepository,
        GitHubClient gitHubClient,
        NotificationSender notificationSender
    ) {
        super(notificationSender);
        this.jdbcLinkRepository = jdbcLinkRepository;
        this.jdbcChatLinkRepository = jdbcChatLinkRepository;
        this.gitHubClient = gitHubClient;
    }

    @Override
    protected ResourceResponse getResponse(GeneralLink link) {
        String[] splitLink = link.getUrl().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        return gitHubClient.fetchRepositoryEvents(owner, repo)
            .orElse(null);
    }

    protected List<Long> getTrackingTgChats(GeneralLink link) {
        return jdbcChatLinkRepository.findAllByLinkId(link.getId())
            .stream().map(ChatLinkDto::chatId).toList();
    }

    @Override
    protected void removeLink(GeneralLink link) {
        jdbcLinkRepository.remove(link.getUrl());
    }

    @Override
    protected void setUpdatedAt(GeneralLink link, OffsetDateTime updatedAt) {
        jdbcLinkRepository.updateLink(link.getUrl(), updatedAt);
    }

    @Override
    protected void setCheckedAt(GeneralLink link) {
        jdbcLinkRepository.setCheckedAt(link.getUrl());
    }

    @Override
    protected String getDescription(ResourceResponse response) {
        GitHubResponse res = (GitHubResponse) response;
        return "Обновление на GitHub!\n"
            + "Пользователь " + res.actor().login() + " внёс изменение " + res.type()
            + " в репозиторий " + res.repo().name();
    }
}
