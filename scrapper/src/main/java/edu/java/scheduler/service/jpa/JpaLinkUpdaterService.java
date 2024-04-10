package edu.java.scheduler.service.jpa;

import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.model.domain.GeneralLink;
import edu.java.scheduler.service.SchedulerService;
import edu.java.scheduler.updater.LinkUpdater;
import java.time.Duration;
import java.util.List;

public class JpaLinkUpdaterService extends SchedulerService {
    private final JpaLinkRepository jpaLinkRepository;

    public JpaLinkUpdaterService(
        JpaLinkRepository jpaLinkRepository,
        List<LinkUpdater> linkUpdaters
    ) {
        super(linkUpdaters);
        this.jpaLinkRepository = jpaLinkRepository;
    }

    @Override
    protected List<GeneralLink> getLinks() {
        return jpaLinkRepository.findLinkByCheckedAt(Duration.ofMinutes(1));
    }
}
