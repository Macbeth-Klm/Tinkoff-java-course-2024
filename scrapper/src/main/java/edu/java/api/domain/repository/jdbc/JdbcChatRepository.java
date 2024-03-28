package edu.java.api.domain.repository.jdbc;

import edu.java.api.domain.repository.ChatRepository;
import edu.java.exceptions.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcChatRepository implements ChatRepository {
    private final JdbcTemplate template;

    @Override
    public void add(Long chatId) {
        template.update(
            "INSERT INTO chat (id) VALUES (?)",
            chatId
        );
    }

    @Override
    public void remove(Long chatId) {
        int deletedRow = template.update(
            "DELETE FROM chat WHERE id = ?",
            chatId
        );
        if (deletedRow == 0) {
            throw new NotFoundException(
                "The user with the given chat id is not registered!",
                "Пользователь не зарегистрирован!"
            );
        }
    }

    @Override
    public List<Long> findAll() {
        return template.query(
            "SELECT * FROM chat",
            (rowSet, rowNum) -> rowSet.getLong("id")
        );
    }

    @Override
    public boolean isRegistered(Long chatId) {
        return Boolean.TRUE.equals(template.queryForObject(
            "SELECT EXISTS(SELECT * FROM chat WHERE id = ?)",
            Boolean.class,
            chatId
        ));
    }
}
