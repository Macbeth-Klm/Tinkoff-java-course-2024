package edu.java.api.domain.repository.jooq;

import edu.java.api.domain.jooq.tables.records.ChatRecord;
import edu.java.api.domain.repository.ChatRepository;
import edu.java.exceptions.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import static edu.java.api.domain.jooq.Tables.CHAT;

@Repository
@RequiredArgsConstructor
public class JooqChatRepository implements ChatRepository {
    private final DSLContext dslContext;

    @Override
    public void add(Long chatId) {
        dslContext.insertInto(CHAT, CHAT.ID)
            .values(chatId)
            .execute();
    }

    @Override
    public void remove(Long chatId) {
        int deletedRow = dslContext.deleteFrom(CHAT)
            .where(CHAT.ID.eq(chatId))
            .execute();
        if (deletedRow == 0) {
            throw new NotFoundException(
                "The user with the given chat id is not registered!",
                "Пользователь не зарегистрирован!"
            );
        }
    }

    @Override
    public List<Long> findAll() {
        return dslContext.selectFrom(CHAT)
            .fetch()
            .map(ChatRecord::getId);
    }

    @Override
    public boolean isRegistered(Long chatId) {
        List<Long> chats = dslContext.selectFrom(CHAT)
            .where(CHAT.ID.eq(chatId))
            .fetch()
            .map(ChatRecord::getId);
        return !chats.isEmpty();
    }
}
