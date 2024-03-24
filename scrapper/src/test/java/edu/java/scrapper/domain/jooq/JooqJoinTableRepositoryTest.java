package edu.java.scrapper.domain.jooq;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.domain.repository.jooq.JooqJoinTableRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
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

@SpringBootTest
public class JooqJoinTableRepositoryTest extends IntegrationTest {
    @Autowired
    private JooqChatRepository jooqChatRepository;
    @Autowired
    private JooqLinkRepository jooqLinkRepository;
    @Autowired
    private JooqJoinTableRepository jooqJoinTableRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);
        jooqJoinTableRepository.add(chatId, linkId);
        List<JoinTableDto> chatsToLinks = jooqJoinTableRepository.findAll();

        assertThat(chatsToLinks).containsOnly(new JoinTableDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);
        jooqJoinTableRepository.add(chatId, linkId);
        Throwable ex = catchThrowable(() -> jooqJoinTableRepository.add(chatId, linkId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);

        jooqJoinTableRepository.add(chatId, linkId);
        jooqJoinTableRepository.remove(chatId, linkId);
        List<JoinTableDto> links = jooqJoinTableRepository.findAll();

        assertThat(links).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);

        Throwable ex = catchThrowable(() -> jooqJoinTableRepository.remove(chatId, linkId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByTgChat() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);
        jooqJoinTableRepository.add(chatId, linkId);

        List<LinkResponse> linkResponseList = jooqJoinTableRepository.findAllByChatId(chatId);

        assertThat(linkResponseList).containsOnly(new LinkResponse(linkId, link));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileFindingByTgChat() {
        Long chatId = 1L;

        Throwable ex = catchThrowable(() -> jooqJoinTableRepository.findAllByChatId(chatId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByLinkId() {
        Long firstChatId = 1L;
        URI firstLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        Long secondChatId = 2L;
        URI secondLink = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2023");
        jooqChatRepository.add(firstChatId);
        Long firstLinkId = jooqLinkRepository.add(firstLink);
        jooqChatRepository.add(secondChatId);
        Long secondLinkId = jooqLinkRepository.add(secondLink);
        jooqJoinTableRepository.add(firstChatId, firstLinkId);
        jooqJoinTableRepository.add(secondChatId, secondLinkId);

        List<JoinTableDto> linkResponseList = jooqJoinTableRepository.findAllByLinkId(secondLinkId);

        assertThat(linkResponseList).containsOnly(new JoinTableDto(secondChatId, secondLinkId));
    }
}
