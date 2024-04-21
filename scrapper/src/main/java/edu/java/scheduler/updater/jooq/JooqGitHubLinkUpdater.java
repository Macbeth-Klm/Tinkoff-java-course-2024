package edu.java.scheduler.updater.jooq;

import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
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

public class JooqGitHubLinkUpdater extends LinkUpdater {
    @Getter
    private final String host = "github.com";
    private final JooqLinkRepository jooqLinkRepository;
    private final JooqChatLinkRepository jooqChatLinkRepository;
    private final GitHubClient gitHubClient;

    public JooqGitHubLinkUpdater(
        JooqLinkRepository jooqLinkRepository,
        JooqChatLinkRepository jooqChatLinkRepository,
        GitHubClient gitHubClient,
        NotificationSender notificationSender
    ) {
        super(notificationSender);
        this.jooqLinkRepository = jooqLinkRepository;
        this.jooqChatLinkRepository = jooqChatLinkRepository;
        this.gitHubClient = gitHubClient;
    }

    @Override
    protected ResourceResponse getResponse(GeneralLink link) {
        String[] splitLink = link.getUrl().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        return gitHubClient.retryFetchRepositoryEvents(owner, repo)
            .orElse(null);
    }

    @Override
    protected List<Long> getTrackingTgChats(GeneralLink link) {
        return jooqChatLinkRepository.findAllByLinkId(link.getId())
            .stream().map(ChatLinkDto::chatId).toList();
    }

    @Override
    protected void removeLink(GeneralLink link) {
        jooqLinkRepository.remove(link.getUrl());
    }

    @Override
    protected void setUpdatedAt(GeneralLink link, OffsetDateTime updatedAt) {
        jooqLinkRepository.updateLink(link.getUrl(), updatedAt);
    }

    @Override
    protected void setCheckedAt(GeneralLink link) {
        jooqLinkRepository.setCheckedAt(link.getUrl());
    }

    @Override
    protected String getDescription(ResourceResponse res) {
        GitHubResponse response = (GitHubResponse) res;
        return "Обновление на GitHub!\n"
            + "Пользователь " + response.actor().login() + " внёс изменение " + response.type()
            + " в репозиторий " + "https://" + host + "/" + response.repo().name();
    }
}
