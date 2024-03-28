package edu.java.api.domain.repository.jdbc;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.api.domain.repository.ChatLinkRepository;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@SuppressWarnings("MultipleStringLiterals")
public class JdbcChatLinkRepository implements ChatLinkRepository {
    private final JdbcTemplate template;

    @Override
    public void add(Long chatId, Long linkId) {
        template.update(
            "INSERT INTO chat_link (chat_id, link_id) VALUES (?, ?)",
            chatId,
            linkId
        );
    }

    @Override
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

    @Override
    public List<JoinTableDto> findAll() {
        return template.query(
            "SELECT * FROM chat_link",
            (rowSet, rowNum) -> new JoinTableDto(rowSet.getLong("chat_id"), rowSet.getLong("link_id"))
        );
    }

    @Override
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

    @Override
    public List<JoinTableDto> findAllByLinkId(Long linkId) {
        return template.query(
            "SELECT * FROM chat_link WHERE link_id = ?",
            (rowSet, rowNum) -> new JoinTableDto(
                rowSet.getLong("chat_id"),
                rowSet.getLong("link_id")
            ),
            linkId
        );
    }
}
