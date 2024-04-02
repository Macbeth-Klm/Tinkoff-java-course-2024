package edu.java.api.domain.repository.jdbc;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.exception.NotFoundException;
import edu.java.model.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class JdbcChatLinkRepository {
    private final JdbcTemplate template;

    public void add(Long chatId, Long linkId) {
        template.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)",
            chatId,
            linkId
        );
    }

    public void remove(Long chatId, Long linkId) {
        int deletedRow = template.update(
            "DELETE FROM chat_link WHERE chat_id = ? AND link_id = ?",
            chatId,
            linkId
        );
        if (deletedRow == 0) {
            throw new NotFoundException(
                "User with the given chat id is not tracking this link",
                "Вы не отслеживаете данную ссылку!"
            );
        }
    }

    public List<ChatLinkDto> findAll() {
        return template.query(
            "SELECT * FROM chat_link",
            (rowSet, rowNum) -> new ChatLinkDto(rowSet.getLong("chat_id"), rowSet.getLong("link_id"))
        );
    }

    public List<LinkResponse> findAllByChatId(Long chatId) {
        return template.query(
            "SELECT id, url FROM link JOIN chat_link ON link.id = chat_link.link_id WHERE chat_link.chat_id = ?",
            (rowSet, rowNum) -> new LinkResponse(
                rowSet.getLong("id"),
                URI.create(rowSet.getString("url"))
            ),
            chatId
        );
    }

    public List<ChatLinkDto> findAllByLinkId(Long linkId) {
        return template.query(
            "SELECT * FROM chat_link WHERE link_id = ?",
            (rowSet, rowNum) -> new ChatLinkDto(
                rowSet.getLong("chat_id"),
                rowSet.getLong("link_id")
            ),
            linkId
        );
    }
}
