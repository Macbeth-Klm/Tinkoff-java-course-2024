package edu.java.scheduler.updater.jooq;

import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.client.StackOverflowClient.StackOverflowClient;
import edu.java.model.domain.GeneralLink;
import edu.java.model.domain.dto.ChatLinkDto;
import edu.java.response.ResourceResponse;
import edu.java.response.StackOverflowResponse;
import edu.java.scheduler.updater.LinkUpdater;
import edu.java.scheduler.updater.NotificationSender;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;

public class JooqStackOverflowLinkUpdater extends LinkUpdater {
    @Getter
    private final String host = "stackoverflow.com";
    private final JooqLinkRepository jooqLinkRepository;
    private final JooqChatLinkRepository jooqChatLinkRepository;
    private final StackOverflowClient stackOverflowClient;

    public JooqStackOverflowLinkUpdater(
        JooqLinkRepository jooqLinkRepository,
        JooqChatLinkRepository jooqChatLinkRepository,
        StackOverflowClient stackOverflowClient,
        NotificationSender notificationSender
    ) {
        super(notificationSender);
        this.jooqLinkRepository = jooqLinkRepository;
        this.jooqChatLinkRepository = jooqChatLinkRepository;
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
        StackOverflowResponse response = (StackOverflowResponse) res;
        return "Обновление на StackOverflow!\n"
            + "На вопрос https://" + host + "/questions/" + response.questionId()
            + " пришёл ответ №" + response.answerId()
            + " от пользователя " + response.owner().displayName();
    }
}
