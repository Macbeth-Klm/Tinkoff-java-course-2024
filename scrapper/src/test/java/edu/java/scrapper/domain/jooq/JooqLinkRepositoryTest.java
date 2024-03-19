package edu.java.scrapper.domain.jooq;

import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.exceptions.NotFoundException;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JooqLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private LinkRepository jooqLinkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqLinkRepository.add(link);
        List<Link> links = jooqLinkRepository.findAll();
        List<URI> uris = links.stream().map(Link::url).toList();
        assertThat(uris).containsOnly(link);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqLinkRepository.add(link);
        jooqLinkRepository.remove(link);
        List<Link> links = jooqLinkRepository.findAll();

        assertThat(links).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        Throwable ex = catchThrowable(() -> jooqLinkRepository.remove(link));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByUrl() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");

        jooqLinkRepository.add(firstLink);
        jooqLinkRepository.add(secondLink);
        Link link = jooqLinkRepository.findByUrl(firstLink);

        assertEquals(firstLink, link.url());
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileFindingByUrl() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        Throwable ex = catchThrowable(() -> jooqLinkRepository.findByUrl(firstLink));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCorrectResultForIsExistLink() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");

        jooqLinkRepository.add(firstLink);
        boolean firstIsExists = jooqLinkRepository.isExist(firstLink);
        boolean secondIsNotExists = jooqLinkRepository.isExist(secondLink);

        assertAll(
            () -> assertTrue(firstIsExists),
            () -> assertFalse(secondIsNotExists)
        );
    }

}
