package edu.java.scheduler.updater.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
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

public class JdbcStackOverflowLinkUpdater extends LinkUpdater {
    @Getter
    private final String host = "stackoverflow.com";
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;
    private final StackOverflowClient stackOverflowClient;

    public JdbcStackOverflowLinkUpdater(
        JdbcLinkRepository jdbcLinkRepository,
        JdbcChatLinkRepository jdbcChatLinkRepository,
        StackOverflowClient stackOverflowClient,
        NotificationSender notificationSender
    ) {
        super(notificationSender);
        this.jdbcLinkRepository = jdbcLinkRepository;
        this.jdbcChatLinkRepository = jdbcChatLinkRepository;
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
        return jdbcChatLinkRepository.findAllByLinkId(link.getId())
            .stream().map(ChatLinkDto::chatId).toList();
    }

    @Override
    protected void removeLink(GeneralLink link) {
        jdbcLinkRepository.remove(link.getUrl());
    }

    @Override
    protected void setUpdatedAt(GeneralLink link, OffsetDateTime updatedAt) {
        jdbcLinkRepository.updateLink(link.getUrl(), updatedAt);
    }

    @Override
    protected void setCheckedAt(GeneralLink link) {
        jdbcLinkRepository.setCheckedAt(link.getUrl());
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
