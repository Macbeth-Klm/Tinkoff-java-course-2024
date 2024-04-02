package edu.java.scrapper.domain.jooq;

import edu.java.api.domain.dto.LinkDto;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JooqLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JooqLinkRepository jooqLinkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqLinkRepository.add(link);
        List<LinkDto> linkDtoList = jooqLinkRepository.findAll();
        List<URI> uris = linkDtoList.stream().map(LinkDto::url).toList();
        assertThat(uris).contains(link);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqLinkRepository.add(link);
        jooqLinkRepository.remove(link);
        Throwable ex = catchThrowable(() -> jooqLinkRepository.findByUrl(link));

        assertThat(ex).isInstanceOf(NullPointerException.class);
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
        Long id = jooqLinkRepository.findByUrl(firstLink);

        assertNotNull(id);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNullPointerExceptionWhileFindingByUrl() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        Throwable ex = catchThrowable(() -> jooqLinkRepository.findByUrl(firstLink));

        assertThat(ex).isInstanceOf(NullPointerException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCorrectResultForIsExistsLink() {
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");

        jooqLinkRepository.add(firstLink);
        boolean firstIsExists = jooqLinkRepository.exists(firstLink);
        boolean secondIsNotExists = jooqLinkRepository.exists(secondLink);

        assertAll(
            () -> assertTrue(firstIsExists),
            () -> assertFalse(secondIsNotExists)
        );
    }
}
