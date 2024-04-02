package edu.java.scheduler.updater.jpa;

import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.model.LinkUpdate;
import edu.java.model.jpa.Chat;
import edu.java.model.jpa.Link;
import edu.java.response.StackOverflowResponse;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JpaStackOverflowLinkUpdater implements JpaLinkUpdater {
    @Getter
    private final String host = "stackoverflow.com";
    private final JpaLinkRepository jpaLinkRepository;
    private final StackOverflowClient stackOverflowClient;
    private final BotClient botClient;

    @Override
    public int process(Link link) {
        String[] splitLink = link.getUrl().split("/");
        long questionId = Long.parseLong(splitLink[splitLink.length - 1]);
        StackOverflowResponse response = stackOverflowClient.fetchQuestionUpdates(questionId)
            .orElse(null);
        List<Long> chats = link.getChats().stream().map(Chat::getId).toList();
        if (chats.isEmpty() || response == null) {
            jpaLinkRepository.delete(link);
            jpaLinkRepository.flush();
            return 1;
        }
        if (link.getUpdatedAt().isBefore(response.lastActivityDate())) {
            link.setUpdatedAt(response.lastActivityDate());
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

    private String getDescription(StackOverflowResponse response) {
        return "Обновление на StackOverflow!\n"
            + "На вопрос №" + response.questionId() + " пришёл ответ №" + response.answerId()
            + " от пользователя " + response.owner().displayName();
    }
}
