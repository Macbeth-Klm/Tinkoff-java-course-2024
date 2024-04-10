package edu.java.api.domain.repository.jdbc;

import edu.java.api.domain.mapper.LinkDtoRowMapper;
import edu.java.exception.NotFoundException;
import edu.java.model.domain.GeneralLink;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcLinkRepository {
    private final JdbcTemplate template;

    public Long add(URI link) {
        return template.queryForObject(
            "INSERT INTO link (url, updated_at, checked_at) VALUES (?, ?, ?) RETURNING id",
            Long.class,
            link.toString(), OffsetDateTime.now(), OffsetDateTime.now()
        );
    }

    public void remove(URI link) {
        int deletedRow = template.update(
            "DELETE FROM link WHERE url = ?",
            link.toString()
        );
        if (deletedRow == 0) {
            throw new NotFoundException(
                "Given link does not exist",
                "Данная ссылка не зарегистрирована"
            );
        }
    }

    public void updateLink(URI url, OffsetDateTime updatedAt) {
        template.update(
            "UPDATE link SET updated_at = ?, checked_at = CURRENT_TIMESTAMP WHERE url = ?",
            updatedAt, url.toString()
        );
    }

    public void setCheckedAt(URI checkedLink) {
        template.update(
            "UPDATE link SET checked_at = CURRENT_TIMESTAMP WHERE url = ?",
            checkedLink.toString()
        );
    }

    public List<GeneralLink> findAll() {
        return template.query(
            "SELECT * FROM link",
            new LinkDtoRowMapper()
        );
    }

    public Long findByUrl(URI link) {
        return template.queryForObject(
            "SELECT id FROM link WHERE url = ?",
            Long.class,
            link.toString()
        );
    }

    public List<GeneralLink> findByCheckedAt(int minutes) {
        return template.query(
            "SELECT * FROM link WHERE CURRENT_TIMESTAMP - checked_at > '" + minutes + " minutes'",
            new LinkDtoRowMapper()
        );
    }

    public boolean exists(URI url) {
        return Boolean.TRUE.equals(template.queryForObject(
            "SELECT EXISTS(SELECT * FROM link WHERE url = ?)",
            Boolean.class,
            url.toString()
        ));
    }
}
