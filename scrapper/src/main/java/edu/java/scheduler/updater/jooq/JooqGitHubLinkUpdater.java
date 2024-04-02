package edu.java.scheduler.updater.jooq;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.GitHubClient.GitHubClient;
import edu.java.models.LinkUpdate;
import edu.java.response.GitHubResponse;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JooqGitHubLinkUpdater implements JooqLinkUpdater {
    @Getter
    private final String host = "github.com";
    private final JooqLinkRepository jooqLinkRepository;
    private final JooqChatLinkRepository jooqChatLinkRepository;
    private final GitHubClient gitHubClient;
    private final BotClient botClient;

    @Override
    public int process(LinkDto linkDto) {
        String[] splitLink = linkDto.url().getPath().split("/");
        String owner = splitLink[splitLink.length - 2];
        String repo = splitLink[splitLink.length - 1];
        GitHubResponse response = gitHubClient.fetchRepositoryEvents(owner, repo)
            .orElse(null);
        List<ChatLinkDto> chatLinkDtoList = jooqChatLinkRepository.findAllByLinkId(linkDto.id());
        if (chatLinkDtoList.isEmpty() || response == null) {
            jooqLinkRepository.remove(linkDto.url());
            return 1;
        }
        if (linkDto.updatedAt().isBefore(response.createdAt())) {
            jooqLinkRepository.updateLink(linkDto.url(), response.createdAt());
            List<Long> tgChatIds = chatLinkDtoList.stream().map(ChatLinkDto::chatId).toList();
            botClient.postUpdates(new LinkUpdate(
                linkDto.id(),
                linkDto.url(),
                getDescription(response),
                tgChatIds
            ));
        } else {
            jooqLinkRepository.setCheckedAt(linkDto.url());
        }
        return 1;
    }

    private String getDescription(GitHubResponse response) {
        return "Обновление на GitHub!\n"
            + "Пользователь " + response.actor().login() + " внёс изменение " + response.type()
            + " в репозиторий " + response.repo().name();
    }
}
