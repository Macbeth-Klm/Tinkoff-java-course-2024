package edu.java.scheduler.updater.jpa;

import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.model.LinkUpdate;
import edu.java.model.jpa.Chat;
import edu.java.model.jpa.Link;
import edu.java.response.GitHubResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaGitHubLinkUpdater implements JpaLinkUpdater {
    @Getter
    private final String host = "github.com";
    private final JpaLinkRepository jpaLinkRepository;
    private final GitHubClient gitHubClient;
    private final BotClient botClient;

    @Override
    public int process(Link link) {
        String[] splitLink = link.getUrl().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        GitHubResponse response = gitHubClient.fetchRepositoryEvents(owner, repo)
            .orElse(null);
        List<Long> chats = link.getChats().stream().map(Chat::getId).toList();
        if (chats.isEmpty() || response == null) {
            jpaLinkRepository.delete(link);
            jpaLinkRepository.flush();
            return 1;
        }
        if (link.getUpdatedAt().isBefore(response.createdAt())) {
            link.setUpdatedAt(response.createdAt());
            botClient.postUpdates(new LinkUpdate(
                link.getId(),
                URI.create(link.getUrl()),
                getDescription(response),
                chats
            ));
        }
        link.setCheckedAt(OffsetDateTime.now());
        jpaLinkRepository.saveAndFlush(link);
        return 1;
    }

    private String getDescription(GitHubResponse response) {
        return "Обновление на GitHub!\n"
            + "Пользователь " + response.actor().login() + " внёс изменение " + response.type()
            + " в репозиторий " + response.repo().name();
    }
}
