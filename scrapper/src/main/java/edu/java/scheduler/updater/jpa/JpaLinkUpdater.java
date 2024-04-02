package edu.java.scheduler.updater.jpa;

import edu.java.model.jpa.Link;

public interface JpaLinkUpdater {
    String getHost();

    int process(Link link);
}
