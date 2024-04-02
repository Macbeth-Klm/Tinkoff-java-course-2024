package edu.java.api.domain.repository.jooq;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.api.domain.jooq.Tables.CHAT_LINK;
import static edu.java.api.domain.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqChatLinkRepository {
    private final DSLContext dslContext;

    public void add(Long chatId, Long linkId) {
        dslContext.insertInto(CHAT_LINK, CHAT_LINK.CHAT_ID, CHAT_LINK.LINK_ID)
            .values(chatId, linkId)
            .execute();
    }

    public void remove(Long chatId, Long linkId) {
        int deletedRow = dslContext.deleteFrom(CHAT_LINK)
            .where(CHAT_LINK.CHAT_ID.eq(chatId).and(CHAT_LINK.LINK_ID.eq(linkId)))
            .execute();
        if (deletedRow == 0) {
            throw new NotFoundException(
                "User with the given chat id is not tracking this link",
                "Вы не отслеживаете данную ссылку!"
            );
        }
    }

    public List<ChatLinkDto> findAll() {
        return dslContext.selectFrom(CHAT_LINK)
            .fetch()
            .map(r -> new ChatLinkDto(
                r.get(CHAT_LINK.CHAT_ID),
                r.get(CHAT_LINK.LINK_ID)
            ));
    }

    public List<LinkResponse> findAllByChatId(Long chatId) {
        return dslContext.select(LINK.ID, LINK.URL)
            .from(LINK)
            .join(CHAT_LINK)
            .on(LINK.ID.eq(CHAT_LINK.LINK_ID))
            .where(CHAT_LINK.CHAT_ID.eq(chatId))
            .fetch()
            .map(r -> new LinkResponse(
                r.get(LINK.ID),
                URI.create(r.get(LINK.URL))
            ));
    }

    public List<ChatLinkDto> findAllByLinkId(Long linkId) {
        return dslContext.selectFrom(CHAT_LINK)
            .where(CHAT_LINK.LINK_ID.eq(linkId))
            .fetch()
            .map(r -> new ChatLinkDto(
                r.get(CHAT_LINK.CHAT_ID),
                r.get(CHAT_LINK.LINK_ID)
            ));
    }
}
