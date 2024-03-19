package edu.java.api.domain.repository.jooq;

import edu.java.api.domain.jooq.Tables;
import edu.java.api.domain.jooq.tables.records.ChatRecord;
import edu.java.api.domain.repository.ChatRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.IntegrityConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {
    private final DSLContext dslContext;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";

    @Override
    @Transactional
    public void add(Long chatId) {
        try {
            dslContext.insertInto(Tables.CHAT, Tables.CHAT.CHAT_ID)
                .values(chatId)
                .execute();
        } catch (IntegrityConstraintViolationException e) {
            throw new BadRequestException(
                "User with the given chat id is already registered",
                "Пользователь уже зарегистрирован"
            );
        } catch (DataAccessException ex) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public void remove(Long chatId) {
        try {
            int deletedRow = dslContext.deleteFrom(Tables.CHAT)
                .where(Tables.CHAT.CHAT_ID.eq(chatId))
                .execute();
            if (deletedRow == 0) {
                throw new NotFoundException(
                    "The user with the given chat id is not registered",
                    "Пользователь не зарегистрирован"
                );
            }
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public List<Long> findAll() {
        try {
            return dslContext.selectFrom(Tables.CHAT)
                .fetch()
                .map(ChatRecord::getChatId);
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public boolean isNotRegistered(Long chatId) {
        try {
            List<Long> chats = dslContext.selectFrom(Tables.CHAT)
                .where(Tables.CHAT.CHAT_ID.eq(chatId))
                .fetch()
                .map(ChatRecord::getChatId);
            return chats.isEmpty();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
