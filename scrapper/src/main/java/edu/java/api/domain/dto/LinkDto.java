package edu.java.api.domain.dto;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDto(
    Long id,
    URI url,
    OffsetDateTime updatedAt,
    OffsetDateTime checkedAt
) {
}
