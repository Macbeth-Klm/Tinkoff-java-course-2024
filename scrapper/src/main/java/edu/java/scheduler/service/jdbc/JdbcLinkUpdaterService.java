package edu.java.scheduler.service.jdbc;

import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.scheduler.service.LinkUpdaterService;
import edu.java.scheduler.updater.jdbc.JdbcLinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkUpdaterService implements LinkUpdaterService {
    private final JdbcLinkRepository jdbcLinkRepository;
    private final List<JdbcLinkUpdater> linkUpdaters;

    @Transactional
    public int update() {
        int count = 0;
        List<LinkDto> links = jdbcLinkRepository.findByCheckedAt(1);
        for (var link : links) {
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
