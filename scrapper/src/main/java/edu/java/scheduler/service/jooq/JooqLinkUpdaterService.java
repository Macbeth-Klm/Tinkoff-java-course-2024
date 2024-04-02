package edu.java.scheduler.service.jooq;

import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.scheduler.service.LinkUpdaterService;
import edu.java.scheduler.updater.jooq.JooqLinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JooqLinkUpdaterService implements LinkUpdaterService {
    private final JooqLinkRepository jooqLinkRepository;
    private final List<JooqLinkUpdater> linkUpdaters;

    @Transactional
    public int update() {
        int count = 0;
        List<LinkDto> linkDtos = jooqLinkRepository.findByCheckedAt(1);
        for (var link : linkDtos) {
            String host = link.url().getHost();
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
