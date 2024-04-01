package edu.java.api.domain.repository.jooq;

import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.exceptions.NotFoundException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import static edu.java.api.domain.jooq.Tables.LINK;

@Repository
@RequiredArgsConstructor
public class JooqLinkRepository implements LinkRepository {
    private final DSLContext dslContext;

    @Override
    public Long add(URI link) {
        return dslContext.insertInto(LINK, LINK.URL, LINK.UPDATED_AT, LINK.CHECKED_AT)
            .values(link.toString(), OffsetDateTime.now(), OffsetDateTime.now())
            .returning(LINK.ID)
            .fetchOne()
            .map(r -> r.get(LINK.ID));
    }

    @Override
    public void remove(URI link) {
        int deletedRow = dslContext.deleteFrom(LINK)
            .where(LINK.URL.eq(link.toString()))
            .execute();
        if (deletedRow == 0) {
            throw new NotFoundException(
                "Given link does not exist",
                "Данная ссылка не зарегистрирована"
            );
        }
    }

    @Override
    public void updateLink(URI url, OffsetDateTime updatedAt) {
        dslContext.update(LINK)
            .set(LINK.UPDATED_AT, updatedAt)
            .set(LINK.CHECKED_AT, OffsetDateTime.now())
            .where(LINK.URL.eq(url.toString()))
            .execute();
    }

    @Override
    public void setCheckedAt(URI checkedLink) {
        dslContext.update(LINK)
            .set(LINK.CHECKED_AT, OffsetDateTime.now())
            .where(LINK.URL.eq(checkedLink.toString()))
            .execute();
    }

    @Override
    public List<Link> findAll() {
        return dslContext.selectFrom(LINK)
            .fetch()
            .map(r -> new Link(
                r.getId(),
                URI.create(r.getUrl()),
                r.getUpdatedAt(),
                r.getCheckedAt()
            ));
    }

    @Override
    public Long findByUrl(URI link) {
        return dslContext.selectFrom(LINK)
            .where(LINK.URL.eq(link.toString()))
            .fetchOne()
            .map(r -> r.get(LINK.ID));
    }

    @Override
    public List<Link> findByCheckedAt(int minutes) {
        Field<Timestamp> diff = DSL.field("current_timestamp - checked_at", Timestamp.class);
        Field<Timestamp> interval = DSL.field("INTERVAL ? MINUTES", Timestamp.class, minutes);
        return dslContext.select()
            .from(LINK)
            .where(diff.gt(interval))
            .fetch()
            .map(r -> new Link(
                r.get(LINK.ID),
                URI.create(r.get(LINK.URL)),
                r.get(LINK.UPDATED_AT),
                r.get(LINK.CHECKED_AT)
            ));
    }

    @Override
    public boolean exists(URI url) {
        List<Long> links = dslContext.selectFrom(LINK)
            .where(LINK.URL.eq(url.toString()))
            .fetch()
            .map(r -> r.get(LINK.ID));
        return !links.isEmpty();
    }
}
