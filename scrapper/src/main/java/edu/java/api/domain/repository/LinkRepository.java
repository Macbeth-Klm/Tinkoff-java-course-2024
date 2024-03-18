package edu.java.api.domain.repository;

import edu.java.api.domain.dto.Link;
import edu.java.api.domain.mapper.LinkDtoRowMapper;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class LinkRepository {
    private final JdbcTemplate template;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";
    private final String findByUrlSqlReq = "SELECT * FROM link WHERE url = ?";

    @Transactional
    public Long add(URI link) {
        try {
            return template.queryForObject(
                "INSERT INTO link (url, updated_at, checked_at) VALUES (?, ?, ?) RETURNING link_id",
                Long.class,
                link.toString(), OffsetDateTime.now(), OffsetDateTime.now()
            );
        } catch (DataAccessException ex) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void remove(URI link) {
        try {
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
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void updateLink(URI url, OffsetDateTime updatedAt) {
        try {
            template.update(
                "UPDATE link SET updated_at = ?, checked_at = current_timestamp WHERE url = ?",
                updatedAt, url.toString()
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public void setCheckedAt(URI checkedLink) {
        try {
            template.update(
                "UPDATE link SET checked_at = current_timestamp WHERE url = ?",
                checkedLink.toString()
            );
        } catch (Exception ex) {
            throw new BadRequestException("Invalid HTTP-request parameters", "Некорректные параметры запроса");
        }
    }

    @Transactional
    public List<Link> findAll() {
        try {
            return template.query(
                "SELECT * FROM link",
                new LinkDtoRowMapper()
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public Link findByUrl(URI link) {
        try {
            List<Link> links = template.query(
                findByUrlSqlReq,
                new LinkDtoRowMapper(),
                link.toString()
            );
            if (links.isEmpty()) {
                String noLinksMessage = "There's not links";
                String noLinksDescription = "Отсутствуют ресурсы";
                throw new NotFoundException(
                    noLinksMessage,
                    noLinksDescription
                );
            }
            return links.getFirst();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public List<Link> findByCheckedAt(int minutes) {
        try {
            return template.query(
                "SELECT * FROM link WHERE current_timestamp - checked_at > '" + minutes + " minutes'",
                new LinkDtoRowMapper()
            );
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Transactional
    public boolean isExist(URI url) {
        try {
            List<Long> links = template.query(
                findByUrlSqlReq,
                (rowSet, rowNum) -> rowSet.getLong("link_id"),
                url.toString()
            );
            return !links.isEmpty();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
