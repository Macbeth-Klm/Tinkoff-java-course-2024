package edu.java.api.domain.repository;

import edu.java.api.domain.dto.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkRepository {
    Long add(URI link);

    void remove(URI link);

    void updateLink(URI url, OffsetDateTime updatedAt);

    void setCheckedAt(URI checkedLink);

    List<Link> findAll();

    Long findByUrl(URI link);

    List<Link> findByCheckedAt(int minutes);

    boolean exists(URI url);
}
