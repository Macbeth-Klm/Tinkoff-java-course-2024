package edu.java.scrapper.domain.jdbc;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.exception.NotFoundException;
import edu.java.model.LinkResponse;
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
public class JdbcChatLinkRepositoryTest extends IntegrationTest {
    @Autowired
    private JdbcChatRepository jdbcChatRepository;
    @Autowired
    private JdbcLinkRepository jdbcLinkRepository;
    @Autowired
    private JdbcChatLinkRepository jdbcChatLinkRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);
        jdbcChatLinkRepository.add(chatId, linkId);
        List<ChatLinkDto> chatsToLinks = jdbcChatLinkRepository.findAll();

        assertThat(chatsToLinks).contains(new ChatLinkDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);
        jdbcChatLinkRepository.add(chatId, linkId);
        Throwable ex = catchThrowable(() -> jdbcChatLinkRepository.add(chatId, linkId));

        assertThat(ex).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);

        jdbcChatLinkRepository.add(chatId, linkId);
        jdbcChatLinkRepository.remove(chatId, linkId);
        List<ChatLinkDto> links = jdbcChatLinkRepository.findAll();

        assertThat(links).doesNotContain(new ChatLinkDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);

        Throwable ex = catchThrowable(() -> jdbcChatLinkRepository.remove(chatId, linkId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByTgChat() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        jdbcChatRepository.add(chatId);
        Long linkId = jdbcLinkRepository.add(link);
        jdbcChatLinkRepository.add(chatId, linkId);

        List<LinkResponse> linkResponseList = jdbcChatLinkRepository.findAllByChatId(chatId);

        assertThat(linkResponseList).containsOnly(new LinkResponse(linkId, link));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileFindingByTgChat() {
        Long chatId = 1000L;

        List<LinkResponse> result = jdbcChatLinkRepository.findAllByChatId(chatId);

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
        jdbcChatRepository.add(firstChatId);
        Long firstLinkId = jdbcLinkRepository.add(firstLink);
        jdbcChatRepository.add(secondChatId);
        Long secondLinkId = jdbcLinkRepository.add(secondLink);
        jdbcChatLinkRepository.add(firstChatId, firstLinkId);
        jdbcChatLinkRepository.add(secondChatId, secondLinkId);

        List<ChatLinkDto> linkResponseList = jdbcChatLinkRepository.findAllByLinkId(secondLinkId);

        assertThat(linkResponseList).contains(new ChatLinkDto(secondChatId, secondLinkId));
    }
}
