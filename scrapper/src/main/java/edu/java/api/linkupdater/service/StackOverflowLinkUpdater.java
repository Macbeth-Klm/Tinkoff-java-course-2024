package edu.java.api.linkupdater.service;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.models.LinkUpdate;
import edu.java.response.StackOverflowResponse;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Component
@RequiredArgsConstructor
@Slf4j
public class StackOverflowLinkUpdater implements LinkUpdater {
    private final String host = "stackoverflow.com";
    private final StackOverflowClient stackOverflowClient;
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final BotClient botClient;

    @Override
    public int process(Link link) {
        String[] splitLink = link.url().getPath().split("/");
        long questionId = Long.parseLong(splitLink[splitLink.length - 1]);
        StackOverflowResponse response = stackOverflowClient.fetchQuestionUpdates(questionId)
            .orElseThrow(IllegalArgumentException::new);
        List<JoinTableDto> joinTableDtos = jdbcChatLinkRepository.findAllByLinkId(link.id());
        if (joinTableDtos.isEmpty()) {
            jdbcLinkRepository.remove(link.url());
            return 1;
        }
        if (link.updatedAt().isAfter(response.lastActivityDate())) {
            List<Long> tgChatIds = joinTableDtos.stream().map(JoinTableDto::chatId).toList();
            botClient.postUpdates(new LinkUpdate(
                link.id(),
                link.url(),
                getDescription(response),
                tgChatIds
            ));
            jdbcLinkRepository.updateLink(link.url(), response.lastActivityDate());
        }
        jdbcLinkRepository.setCheckedAt(link.url());
        return 1;
    }

    private String getDescription(StackOverflowResponse response) {
        return "На вопрос " + response.questionId() + " пришел новый ответ на Stackoverflow от "
            + response.owner().displayName();
    }
}
