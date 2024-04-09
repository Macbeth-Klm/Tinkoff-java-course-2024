package edu.java.scheduler.service.jooq;

import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.model.domain.GeneralLink;
import edu.java.scheduler.service.SchedulerService;
import edu.java.scheduler.updater.LinkUpdater;
import java.util.List;

public class JooqLinkUpdaterService extends SchedulerService {
    private final JooqLinkRepository jooqLinkRepository;

    public JooqLinkUpdaterService(
        JooqLinkRepository jooqLinkRepository,
        List<LinkUpdater> linkUpdaters
    ) {
        super(linkUpdaters);
        this.jooqLinkRepository = jooqLinkRepository;
    }

    @Override
    protected List<GeneralLink> getLinks() {
        return jooqLinkRepository.findByCheckedAt(1);
    }
}
