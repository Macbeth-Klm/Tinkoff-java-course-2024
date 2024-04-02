package edu.java.scheduler.updater.jpa;

import edu.java.models.jpa.Link;

public interface JpaLinkUpdater {
    String getHost();

    int process(Link link);
}
