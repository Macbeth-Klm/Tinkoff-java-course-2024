package edu.java.api.service.linkupdater;

import edu.java.Responses.GitHubResponse;
import edu.java.api.domain.dto.JoinTableDto;
import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.JoinTableRepository;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.clients.BotClient.BotClient;
import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.models.LinkUpdate;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
public class GitHubLinkUpdater implements LinkUpdater {
    private final String host = "github.com";
    private final GitHubClient gitHubClient;
    private final LinkRepository linkRepository;
    private final JoinTableRepository joinTableRepository;
    private final BotClient botClient;

    @Override
    public int process(Link link) {
        String[] splitLink = link.url().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        GitHubResponse response = gitHubClient.fetchRepositoryEvents(owner, repo)
            .orElseThrow(IllegalArgumentException::new);
        if (link.updatedAt().isAfter(response.createdAt())) {
            List<JoinTableDto> joinTableDtos = joinTableRepository.findAllByLinkId(link.id());
            if (joinTableDtos.isEmpty()) {
                linkRepository.remove(link.url());
                return 1;
            }
            List<Long> tgChatIds = joinTableDtos.stream().map(JoinTableDto::chatId).toList();
            botClient.postUpdates(new LinkUpdate(
                link.id(),
                link.url(),
                getDescription(response),
                tgChatIds
            ));
        }
        return 1;
    }

    private String getDescription(GitHubResponse response) {
        return "Произошло обновление типа " + response.type()
            + " в репозитории " + response.repo() + " от автора "
            + response.actor();
    }
}
