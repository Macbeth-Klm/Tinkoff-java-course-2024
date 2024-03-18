package edu.java.api.service.linkupdater;

import edu.java.api.domain.dto.Link;

public interface LinkUpdater {
    String getHost();
    int process(Link link);
}
