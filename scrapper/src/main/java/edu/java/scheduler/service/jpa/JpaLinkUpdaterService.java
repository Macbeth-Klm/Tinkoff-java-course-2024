package edu.java.scheduler.service.jpa;

import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.models.jpa.Link;
import edu.java.scheduler.service.LinkUpdaterService;
import edu.java.scheduler.updater.jpa.JpaLinkUpdater;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkUpdaterService implements LinkUpdaterService {
    private final JpaLinkRepository jpaLinkRepository;
    private final List<JpaLinkUpdater> linkUpdaters;

    @Transactional
    public int update() {
        int count = 0;
        List<Link> links = jpaLinkRepository.findLinkByCheckedAt(Duration.ofMinutes(1));
        for (var link : links) {
            String host = link.getUrl().split("/")[2];
            for (var linkUpdater : linkUpdaters) {
                if (host.equals(linkUpdater.getHost())) {
                    count += linkUpdater.process(link);
                    break;
                }
            }
        }
        return count;
    }
}
