package edu.java.scrapper.domain;

import edu.java.api.domain.dto.Link;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.exceptions.NotFoundException;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JdbcLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private LinkRepository jdbcLinkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jdbcLinkRepository.add(link);
        List<Link> links = jdbcLinkRepository.findAll();
        List<URI> uris = links.stream().map(Link::url).toList();
        assertThat(uris).contains(link);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jdbcLinkRepository.add(link);
        jdbcLinkRepository.remove(link);
        Throwable ex = catchThrowable(() -> jdbcLinkRepository.findByUrl(link));

        assertThat(ex).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        Throwable ex = catchThrowable(() -> jdbcLinkRepository.remove(link));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByUrl() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");

        jdbcLinkRepository.add(firstLink);
        jdbcLinkRepository.add(secondLink);
        Long id = jdbcLinkRepository.findByUrl(firstLink);

        assertEquals(10, id);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowEmptyResultDataAccessExceptionWhileFindingByUrl() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        Throwable ex = catchThrowable(() -> jdbcLinkRepository.findByUrl(firstLink));

        assertThat(ex).isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCorrectResultForIsExistsLink() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");

        jdbcLinkRepository.add(firstLink);
        boolean firstIsExists = jdbcLinkRepository.exists(firstLink);
        boolean secondIsNotExists = jdbcLinkRepository.exists(secondLink);

        assertAll(
            () -> assertTrue(firstIsExists),
            () -> assertFalse(secondIsNotExists)
        );
    }

}
