package edu.java.scheduler.updater;

import edu.java.client.BotClient.BotClient;
import edu.java.model.LinkUpdate;
import edu.java.model.domain.GeneralLink;
import edu.java.response.ResourceResponse;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class LinkUpdater {
    private final NotificationSender notificationSender;

    public abstract String getHost();

    public int process(GeneralLink link) {
        ResourceResponse res = getResponse(link);
        List<Long> tgChatIds = getTrackingTgChats(link);
        if (tgChatIds.isEmpty() || res == null) {
            removeLink(link);
            return 1;
        }
        OffsetDateTime responseUpdatedAt = res.getUpdatedAt();
        if (link.getUpdatedAt().isBefore(responseUpdatedAt)) {
            setUpdatedAt(link, responseUpdatedAt);
            notificationSender.sendUpdate(new LinkUpdate(
                link.getId(),
                link.getUrl(),
                getDescription(res),
                tgChatIds
            ));
        } else {
            setCheckedAt(link);
        }
        return 1;
    }

    protected abstract ResourceResponse getResponse(GeneralLink link);

    protected abstract List<Long> getTrackingTgChats(GeneralLink link);

    protected abstract void removeLink(GeneralLink link);

    protected abstract void setUpdatedAt(GeneralLink link, OffsetDateTime updatedAt);

    protected abstract void setCheckedAt(GeneralLink link);

    protected abstract String getDescription(ResourceResponse res);
}
