package edu.java.api.service;

import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.api.service.linkupdater.LinkUpdater;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkUpdateService {
    private final LinkRepository linkRepository;
    private final List<LinkUpdater> linkUpdaters;

    public int update() {
        int count = 0;
        List<Link> links = linkRepository.findByCheckedAt(5);
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
