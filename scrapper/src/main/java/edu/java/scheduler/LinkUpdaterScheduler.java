package edu.java.scheduler;

import edu.java.scheduler.service.LinkUpdaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdaterScheduler {
    private final LinkUpdaterService linkUpdaterService;

    @Scheduled(fixedDelayString = "${app.scheduler.interval}")
    public void update() {
        log.info("START UPDATE");
        int checkedLinksCount = linkUpdaterService.update();
        log.info("{} links have been checked", checkedLinksCount);
    }
}
