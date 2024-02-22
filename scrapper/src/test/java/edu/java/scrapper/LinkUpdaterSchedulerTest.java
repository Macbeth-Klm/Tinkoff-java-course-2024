package edu.java.scrapper;

import edu.java.LinkUpdaterScheduler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class LinkUpdaterSchedulerTest {
    @Test
    public void shouldCorrectUpdate() {
        var linkUpdaterScheduler = Mockito.mock(LinkUpdaterScheduler.class);
        Mockito.doThrow(new RuntimeException()).when(linkUpdaterScheduler).update();

        Assertions.assertThrows(RuntimeException.class, linkUpdaterScheduler::update);
    }
}
