package edu.java.scheduler.updater.jdbc;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.models.LinkUpdate;
import edu.java.response.StackOverflowResponse;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcStackOverflowLinkUpdater implements JdbcLinkUpdater {
    @Getter
    private final String host = "stackoverflow.com";
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Override
    public int process(LinkDto linkDto) {
        String[] splitLink = linkDto.url().getPath().split("/");
        long questionId = Long.parseLong(splitLink[splitLink.length - 1]);
        StackOverflowResponse response = stackOverflowClient.fetchQuestionUpdates(questionId)
            .orElse(null);
        List<ChatLinkDto> chatLinkDtoList = jdbcChatLinkRepository.findAllByLinkId(linkDto.id());
        if (chatLinkDtoList.isEmpty() || response == null) {
            jdbcLinkRepository.remove(linkDto.url());
            return 1;
        }
        if (linkDto.updatedAt().isBefore(response.lastActivityDate())) {
            jdbcLinkRepository.updateLink(linkDto.url(), response.lastActivityDate());
            List<Long> tgChatIds = chatLinkDtoList.stream().map(ChatLinkDto::chatId).toList();
            botClient.postUpdates(new LinkUpdate(
                linkDto.id(),
                linkDto.url(),
                getDescription(response),
                tgChatIds
            ));
        } else {
            jdbcLinkRepository.setCheckedAt(linkDto.url());
        }
        return 1;
    }

    private String getDescription(StackOverflowResponse response) {
        return "Обновление на StackOverflow!\n"
            + "На вопрос №" + response.questionId() + " пришёл ответ №" + response.answerId()
            + " от пользователя " + response.owner().displayName();
    }
}
