package edu.java.scheduler.updater.jooq;

import edu.java.api.domain.dto.LinkDto;

public interface JooqLinkUpdater {
    String getHost();

    int process(LinkDto linkDto);
}
