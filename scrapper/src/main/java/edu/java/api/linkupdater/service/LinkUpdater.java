package edu.java.api.linkupdater.service;

import edu.java.api.domain.dto.Link;

public interface LinkUpdater {
    String getHost();

    int process(Link link);
}
