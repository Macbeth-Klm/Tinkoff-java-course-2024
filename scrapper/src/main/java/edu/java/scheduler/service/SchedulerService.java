package edu.java.scheduler.service;

import edu.java.model.domain.GeneralLink;
import edu.java.scheduler.updater.LinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public abstract class SchedulerService {
    protected final List<LinkUpdater> linkUpdaters;

    @Transactional
    public int update() {
        int count = 0;
        List<GeneralLink> links = getLinks();
        for (var link : links) {
            String host = link.getHost();
            for (var linkUpdater : linkUpdaters) {
                if (host.equals(linkUpdater.getHost())) {
                    count += linkUpdater.process(link);
                    break;
                }
            }
        }
        return count;
    }

    protected abstract List<GeneralLink> getLinks();
}
