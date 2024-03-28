package edu.java.api.linkupdater.service;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.models.LinkUpdate;
import edu.java.response.GitHubResponse;
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
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final BotClient botClient;

    @Override
    public int process(Link link) {
        String[] splitLink = link.url().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        GitHubResponse response = gitHubClient.fetchRepositoryEvents(owner, repo)
            .orElseThrow(IllegalArgumentException::new);
        List<ChatLinkDto> chatLinkDtos = jdbcChatLinkRepository.findAllByLinkId(link.id());
        if (chatLinkDtos.isEmpty()) {
            jdbcLinkRepository.remove(link.url());
            return 1;
        }
        if (link.updatedAt().isAfter(response.createdAt())) {
            List<Long> tgChatIds = chatLinkDtos.stream().map(ChatLinkDto::chatId).toList();
            botClient.postUpdates(new LinkUpdate(
                link.id(),
                link.url(),
                getDescription(response),
                tgChatIds
            ));
            jdbcLinkRepository.updateLink(link.url(), response.createdAt());

        }
        jdbcLinkRepository.setCheckedAt(link.url());
        return 1;
    }

    /* Метод реализует задачу 1 из hw5-bonus, но я написал его в hw5, исходя из полей LinkUpdate и response
     каждого клиента, реализованного в предыдущих дз, и из того, что LinkUpdate.description пойдет в качестве
     ответа пользователю из скелета бота. Поэтому оставлю тут коммент, чтобы он пошёл в hw5-bonus PR
     */
    private String getDescription(GitHubResponse response) {
        return "Обновление на GitHub!\n"
            + "Пользователь " + response.actor().login() + " внёс изменение " + response.type()
            + " в репозиторий " + response.repo().name();
    }
}
