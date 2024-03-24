package edu.java.api.domain.repository.jooq;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import static edu.java.api.domain.jooq.Tables.LINK;
import static edu.java.api.domain.jooq.Tables.LINK_CHAT_JOIN_TABLE;

@Repository
@RequiredArgsConstructor
public class JooqJoinTableRepository {
    private final DSLContext dslContext;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";

    public void add(Long chatId, Long linkId) {
        try {
            dslContext.insertInto(LINK_CHAT_JOIN_TABLE, LINK_CHAT_JOIN_TABLE.CHAT_ID, LINK_CHAT_JOIN_TABLE.LINK_ID)
                .values(chatId, linkId)
                .execute();
        } catch (IntegrityConstraintViolationException e) {
            throw new BadRequestException(
                "User with the given chat id is already tracking this link",
                "Пользователь уже отслеживает данную ссылку"
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    public void remove(Long chatId, Long linkId) {
        try {
            int deletedRow = dslContext.deleteFrom(LINK_CHAT_JOIN_TABLE)
                .where(LINK_CHAT_JOIN_TABLE.CHAT_ID.eq(chatId).and(LINK_CHAT_JOIN_TABLE.LINK_ID.eq(linkId)))
                .execute();
            if (deletedRow == 0) {
                throw new NotFoundException(
                    "User with the given chat id is not tracking this link",
                    "Пользователь не отслеживает данную ссылку"
                );
            }
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    public List<JoinTableDto> findAll() {
        try {
            return dslContext.selectFrom(LINK_CHAT_JOIN_TABLE)
                .fetch()
                .map(r -> new JoinTableDto(
                    r.get(LINK_CHAT_JOIN_TABLE.CHAT_ID),
                    r.get(LINK_CHAT_JOIN_TABLE.LINK_ID)
                ));
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    public List<LinkResponse> findAllByChatId(Long chatId) {
        try {
            List<LinkResponse> responses = dslContext.select(LINK.LINK_ID, LINK.URL)
                .from(LINK)
                .join(LINK_CHAT_JOIN_TABLE)
                .on(LINK.LINK_ID.eq(LINK_CHAT_JOIN_TABLE.LINK_ID))
                .where(LINK_CHAT_JOIN_TABLE.CHAT_ID.eq(chatId))
                .fetch()
                .map(r -> new LinkResponse(
                    r.get(LINK.LINK_ID),
                    URI.create(r.get(LINK.URL))
                ));
            if (responses.isEmpty()) {
                throw new NotFoundException(
                    "User has no subscribes",
                    "У пользователя отсутствуют подписки"
                );
            }
            return responses;
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    public List<JoinTableDto> findAllByLinkId(Long linkId) {
        try {
            return dslContext.selectFrom(LINK_CHAT_JOIN_TABLE)
                .where(LINK_CHAT_JOIN_TABLE.LINK_ID.eq(linkId))
                .fetch()
                .map(r -> new JoinTableDto(
                    r.get(LINK_CHAT_JOIN_TABLE.CHAT_ID),
                    r.get(LINK_CHAT_JOIN_TABLE.LINK_ID)
                ));
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
