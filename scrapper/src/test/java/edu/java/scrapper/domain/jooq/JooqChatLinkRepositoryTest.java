package edu.java.scrapper.domain.jooq;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import edu.java.scrapper.IntegrationTest;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
public class JooqChatLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JooqChatRepository jooqChatRepository;
    @Autowired
    private JooqLinkRepository jooqLinkRepository;
    @Autowired
    private JooqChatLinkRepository jooqChatLinkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);
        jooqChatLinkRepository.add(chatId, linkId);
        List<ChatLinkDto> chatsToLinks = jooqChatLinkRepository.findAll();

        assertThat(chatsToLinks).contains(new ChatLinkDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);
        jooqChatLinkRepository.add(chatId, linkId);
        Throwable ex = catchThrowable(() -> jooqChatLinkRepository.add(chatId, linkId));

        assertThat(ex).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);

        jooqChatLinkRepository.add(chatId, linkId);
        jooqChatLinkRepository.remove(chatId, linkId);
        List<ChatLinkDto> links = jooqChatLinkRepository.findAll();

        assertThat(links).doesNotContain(new ChatLinkDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jooqChatRepository.add(chatId);
        Long linkId = jooqLinkRepository.add(link);

        Throwable ex = catchThrowable(() -> jooqChatLinkRepository.remove(chatId, linkId));

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
        jooqChatLinkRepository.add(chatId, linkId);

        List<LinkResponse> linkResponseList = jooqChatLinkRepository.findAllByChatId(chatId);

        assertThat(linkResponseList).contains(new LinkResponse(linkId, link));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileFindingByTgChat() {
        Long chatId = 1000L;

        List<LinkResponse> result = jooqChatLinkRepository.findAllByChatId(chatId);

        assertThat(result).isEmpty();
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
        jooqChatLinkRepository.add(firstChatId, firstLinkId);
        jooqChatLinkRepository.add(secondChatId, secondLinkId);

        List<ChatLinkDto> linkResponseList = jooqChatLinkRepository.findAllByLinkId(secondLinkId);

        assertThat(linkResponseList).contains(new ChatLinkDto(secondChatId, secondLinkId));
    }
}
