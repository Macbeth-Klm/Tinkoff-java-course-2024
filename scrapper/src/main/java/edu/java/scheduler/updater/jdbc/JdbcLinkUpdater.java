package edu.java.scheduler.updater.jdbc;

import edu.java.api.domain.dto.LinkDto;

public interface JdbcLinkUpdater {
    String getHost();

    int process(LinkDto linkDto);
}
