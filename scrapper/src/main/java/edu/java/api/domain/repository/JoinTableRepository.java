package edu.java.api.domain.repository;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class JoinTableRepository {
    private final JdbcTemplate template;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";

    @Transactional
    public void addRecord(Long chatId, Long linkId) {
        try {
            template.update(
                "INSERT INTO link_chat_join_table (chat_id, link_id) VALUES (?, ?)",
                chatId,
                linkId
            );
        } catch (DuplicateKeyException e) {
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

    @Transactional
    public void deleteRecord(Long chatId, Long linkId) {
        try {
            int deletedRow = template.update(
                "DELETE FROM link_chat_join_table WHERE chat_id = ? AND link_id = ?",
                chatId,
                linkId
            );
            if (deletedRow == 0) {
                throw new BadRequestException(
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

    @Transactional
    public Optional<List<JoinTableDto>> findAll() {
        try {
            List<JoinTableDto> records = template.query(
                "SELECT * FROM link_chat_join_table",
                (rowSet, rowNum) -> new JoinTableDto(rowSet.getLong("chat_id"), rowSet.getLong("link_id"))
            );
            return Optional.of(records);
        } catch (Exception e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public List<LinkResponse> findAllByChatId(Long chatId) {
        try {
            List<LinkResponse> responses = template.query(
                "SELECT link_id, url FROM ((SELECT * FROM link_chat_join_table WHERE chat_id = ?) AS subtable JOIN link USING (link_id))",
                (rowSet, rowNum) -> new LinkResponse(
                    rowSet.getLong("link_id"),
                    URI.create(rowSet.getString("url"))
                ),
                chatId
            );
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

    @Transactional
    public List<JoinTableDto> findAllByLinkId(Long linkId) {
        try {
            return template.query(
                "SELECT * FROM link_chat_join_table WHERE link_id = ?",
                (rowSet, rowNum) -> new JoinTableDto(
                    rowSet.getLong("chat_id"),
                    rowSet.getLong("link_id")
                ),
                linkId
            );
        } catch (Exception e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
