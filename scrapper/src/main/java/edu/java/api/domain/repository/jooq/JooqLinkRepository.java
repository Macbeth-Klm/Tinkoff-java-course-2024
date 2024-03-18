package edu.java.api.domain.repository.jooq;

import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.api.domain.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;
    private final String dataAccessMessage = "Server error";
    private final String dataAccessDescription = "Ошибка сервера: нет доступа к данным";

    @Override
    @Transactional
    public Long add(URI link) {
        try {
            return dslContext.insertInto(LINK, LINK.URL, LINK.UPDATED_AT, LINK.CHECKED_AT)
                .values(link.toString(), OffsetDateTime.now(), OffsetDateTime.now())
                .returning(LINK.LINK_ID)
                .fetchInto(Long.class)
                .getFirst();
        } catch (DataAccessException ex) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public void remove(URI link) {
        try {
            int deletedRow = dslContext.deleteFrom(LINK)
                .where(LINK.URL.eq(link.toString()))
                .execute();
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

    @Override
    @Transactional
    public void updateLink(URI url, OffsetDateTime updatedAt) {
        try {
            dslContext.update(LINK)
                .set(LINK.UPDATED_AT, updatedAt)
                .set(LINK.CHECKED_AT, OffsetDateTime.now())
                .where(LINK.URL.eq(url.toString()))
                .execute();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public void setCheckedAt(URI checkedLink) {
        try {
            dslContext.update(LINK)
                .set(LINK.CHECKED_AT, OffsetDateTime.now())
                .where(LINK.URL.eq(checkedLink.toString()))
                .execute();
        } catch (Exception ex) {
            throw new BadRequestException("Invalid HTTP-request parameters", "Некорректные параметры запроса");
        }
    }

    @Override
    @Transactional
    public List<Link> findAll() {
        try {
            return dslContext.selectFrom(LINK)
                .fetch()
                .map(r -> new Link(
                    r.getLinkId(),
                    URI.create(r.getUrl()),
                    r.getUpdatedAt(),
                    r.getCheckedAt()
                ));
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public Link findByUrl(URI link) {
        try {
            List<Link> links = dslContext.selectFrom(LINK)
                .where(LINK.URL.eq(link.toString()))
                .fetch()
                .map(r -> new Link(
                    r.get(LINK.LINK_ID),
                    URI.create(r.get(LINK.URL)),
                    r.get(LINK.UPDATED_AT),
                    r.get(LINK.CHECKED_AT)
                ));
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

    @Override
    @Transactional
    public List<Link> findByCheckedAt(int minutes) {
        try {
            Field<Timestamp> diff = DSL.field("current_timestamp - checked_at", Timestamp.class);
            Field<Timestamp> interval = DSL.field("INTERVAL ? MINUTES", Timestamp.class, minutes);
            return dslContext.select()
                .from(LINK)
                .where(diff.gt(interval))
                .fetch()
                .map(r -> new Link(
                    r.get(LINK.LINK_ID),
                    URI.create(r.get(LINK.URL)),
                    r.get(LINK.UPDATED_AT),
                    r.get(LINK.CHECKED_AT)
                ));
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }

    @Override
    @Transactional
    public boolean isExist(URI url) {
        try {
            List<Long> links = dslContext.selectFrom(LINK)
                .where(LINK.URL.eq(url.toString()))
                .fetch()
                .map(r -> r.get(LINK.LINK_ID));
            return !links.isEmpty();
        } catch (DataAccessException e) {
            throw new BadRequestException(
                dataAccessMessage,
                dataAccessDescription
            );
        }
    }
}
