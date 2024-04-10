package edu.java.model.domain.dto;

import edu.java.model.domain.GeneralLink;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LinkDto implements GeneralLink {
    private Long id;
    private URI url;
    private OffsetDateTime updatedAt;
    private OffsetDateTime checkedAt;

    @Override
    public String getHost() {
        return url.getHost();
    }
}
