package edu.java.scheduler.updater.jpa;

import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.model.domain.GeneralLink;
import edu.java.model.domain.jpa.Chat;
import edu.java.model.domain.jpa.Link;
import edu.java.response.GitHubResponse;
import edu.java.response.ResourceResponse;
import edu.java.scheduler.updater.LinkUpdater;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JpaGitHubLinkUpdater extends LinkUpdater {
    @Getter
    private final String host = "github.com";
    private final JpaLinkRepository jpaLinkRepository;
    private final GitHubClient gitHubClient;

    public JpaGitHubLinkUpdater(
        JpaLinkRepository jpaLinkRepository,
        GitHubClient gitHubClient,
        BotClient botClient
    ) {
        super(botClient);
        this.jpaLinkRepository = jpaLinkRepository;
        this.gitHubClient = gitHubClient;
    }

    @Override
    protected ResourceResponse getResponse(GeneralLink link) {
        log.info("JpaGitHubLinkUpdater: getting response");
        String[] splitLink = link.getUrl().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        log.info("owner: {}; repo: {}", owner, repo);
        return gitHubClient.fetchRepositoryEvents(owner, repo)
            .orElse(null);
    }

    @Override
    protected List<Long> getTrackingTgChats(GeneralLink link) {
        Link jpaLink = (Link) link;
        return jpaLink.getChats().stream().map(Chat::getId).toList();
    }

    @Override
    protected void removeLink(GeneralLink link) {
        Link jpaLink = (Link) link;
        jpaLinkRepository.delete(jpaLink);
        jpaLinkRepository.flush();
    }

    @Override
    protected void setUpdatedAt(GeneralLink link, OffsetDateTime updatedAt) {
        Link jpaLink = (Link) link;
        jpaLink.setUpdatedAt(updatedAt);
        jpaLinkRepository.saveAndFlush(jpaLink);
    }

    @Override
    protected void setCheckedAt(GeneralLink link) {
        Link jpaLink = (Link) link;
        jpaLink.setCheckedAt(OffsetDateTime.now());
        jpaLinkRepository.saveAndFlush(jpaLink);
    }

    @Override
    protected String getDescription(ResourceResponse res) {
        GitHubResponse response = (GitHubResponse) res;
        return "Обновление на GitHub!\n"
            + "Пользователь " + response.actor().login() + " внёс изменение " + response.type()
            + " в репозиторий " + response.repo().name();
    }
}
