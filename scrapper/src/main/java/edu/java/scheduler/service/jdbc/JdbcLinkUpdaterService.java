package edu.java.scheduler.service.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.model.domain.GeneralLink;
import edu.java.scheduler.service.SchedulerService;
import edu.java.scheduler.updater.LinkUpdater;
import java.util.List;

public class JdbcLinkUpdaterService extends SchedulerService {
    private final JdbcLinkRepository jdbcLinkRepository;

    public JdbcLinkUpdaterService(
        JdbcLinkRepository jdbcLinkRepository,
        List<LinkUpdater> linkUpdaters
    ) {
        super(linkUpdaters);
        this.jdbcLinkRepository = jdbcLinkRepository;
    }

    @Override
    protected List<GeneralLink> getLinks() {
        return jdbcLinkRepository.findByCheckedAt(1);
    }
}
