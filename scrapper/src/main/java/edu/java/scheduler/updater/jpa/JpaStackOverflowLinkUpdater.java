package edu.java.scheduler.updater.jpa;

import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.client.BotClient.BotClient;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.model.domain.GeneralLink;
import edu.java.model.domain.jpa.Chat;
import edu.java.model.domain.jpa.Link;
import edu.java.response.ResourceResponse;
import edu.java.response.StackOverflowResponse;
import edu.java.scheduler.updater.LinkUpdater;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;

public class JpaStackOverflowLinkUpdater extends LinkUpdater {
    @Getter
    private final String host = "stackoverflow.com";
    private final JpaLinkRepository jpaLinkRepository;
    private final StackOverflowClient stackOverflowClient;

    public JpaStackOverflowLinkUpdater(
        JpaLinkRepository jpaLinkRepository,
        StackOverflowClient stackOverflowClient,
        BotClient botClient
    ) {
        super(botClient);
        this.jpaLinkRepository = jpaLinkRepository;
        this.stackOverflowClient = stackOverflowClient;
    }

    @Override
    protected ResourceResponse getResponse(GeneralLink link) {
        String[] splitLink = link.getUrl().getPath().split("/");
        long questionId = Long.parseLong(splitLink[splitLink.length - 1]);
        return stackOverflowClient.fetchQuestionUpdates(questionId)
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
        StackOverflowResponse response = (StackOverflowResponse) res;
        return "Обновление на StackOverflow!\n"
            + "На вопрос №" + response.questionId() + " пришёл ответ №" + response.answerId()
            + " от пользователя " + response.owner().displayName();
    }
}
