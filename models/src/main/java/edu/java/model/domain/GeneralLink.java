package edu.java.model.domain;

import java.net.URI;
import java.time.OffsetDateTime;

public interface GeneralLink {
    Long getId();

    URI getUrl();

    OffsetDateTime getUpdatedAt();

    String getHost();

    void setUrl(URI url);

    void setUpdatedAt(OffsetDateTime updatedAt);

    void setCheckedAt(OffsetDateTime checkedAt);
}
