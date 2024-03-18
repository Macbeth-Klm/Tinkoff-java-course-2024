package edu.java.api.domain.repository;

import edu.java.api.domain.mapper.LinkDtoRowMapper;
import edu.java.api.domain.dto.Link;
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
                "DELETE FROM link WHERE url = ? RETURNING link_id",
                link.toString()
            );
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
    public void setCheckedAt(List<URI> checkedLinks) {
        List<String> links = checkedLinks.stream().map(URI::toString).toList();
        try {
            for (String link : links) {
                template.update(
                    "UPDATE link SET checked_at = current_timestamp WHERE url = ?",
                    link
                );
            }
        } catch (Exception ex) {
            throw new BadRequestException("Invalid HTTP-request parameters", "Некорректные параметры запроса");
        }
    }

    @Transactional
    public List<Link> findAll() {
        try {
            List<Link> links = template.query(
                "SELECT * FROM link",
                new LinkDtoRowMapper()
            );
            if (links.isEmpty()) {
                throw new NotFoundException(
                    "There's not links",
                    "Отсутствуют ресурсы"
                );
            }
            return links;
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
                "SELECT * FROM link WHERE url = ?",
                new LinkDtoRowMapper(),
                link.toString()
            );
            if (links.isEmpty()) {
                throw new NotFoundException(
                    "There's not links",
                    "Отсутствуют ресурсы"
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
            List<Link> linkList = template.query(
                "SELECT * FROM link WHERE current_timestamp - checked_at > '" + minutes + " seconds'",
                new LinkDtoRowMapper()
            );
            return linkList;
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
                "SELECT * FROM link WHERE url = ?",
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
