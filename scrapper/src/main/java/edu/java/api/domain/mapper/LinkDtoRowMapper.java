package edu.java.api.domain.mapper;

import edu.java.api.domain.dto.LinkDto;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import org.springframework.jdbc.core.RowMapper;

public class LinkDtoRowMapper implements RowMapper<LinkDto> {
    @Override
    public LinkDto mapRow(ResultSet rowSet, int rowNum) throws SQLException {
        Long linkId = rowSet.getLong("id");
        String url = rowSet.getString("url");
        OffsetDateTime updatedAt = rowSet.getObject("updated_at", OffsetDateTime.class);
        OffsetDateTime checkedAt = rowSet.getObject("checked_at", OffsetDateTime.class);
        return new LinkDto(linkId, URI.create(url), updatedAt, checkedAt);
    }
}
