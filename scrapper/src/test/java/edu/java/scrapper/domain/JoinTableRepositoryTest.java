package edu.java.scrapper.domain;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.api.domain.repository.ChatRepository;
import edu.java.api.domain.repository.JoinTableRepository;
import edu.java.api.domain.repository.LinkRepository;
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
public class JoinTableRepositoryTest extends IntegrationTest {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private JoinTableRepository joinTableRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        chatRepository.add(chatId);
        Long linkId = linkRepository.add(link);
        joinTableRepository.add(chatId, linkId);
        List<JoinTableDto> chatsToLinks = joinTableRepository.findAll();

        assertThat(chatsToLinks).containsOnly(new JoinTableDto(chatId, linkId));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingRecord() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");

        chatRepository.add(chatId);
        Long linkId = linkRepository.add(link);
        joinTableRepository.add(chatId, linkId);
        Throwable ex = catchThrowable(() -> joinTableRepository.add(chatId, linkId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        chatRepository.add(chatId);
        Long linkId = linkRepository.add(link);

        joinTableRepository.add(chatId, linkId);
        joinTableRepository.remove(chatId, linkId);
        List<JoinTableDto> links = joinTableRepository.findAll();

        assertThat(links).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingLink() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        chatRepository.add(chatId);
        Long linkId = linkRepository.add(link);

        Throwable ex = catchThrowable(() -> joinTableRepository.remove(chatId, linkId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldFindByTgChat() {
        Long chatId = 1L;
        URI link = URI.create("https://github.com/Macbeth-Klm/Tinkoff-java-course-2024");
        chatRepository.add(chatId);
        Long linkId = linkRepository.add(link);
        joinTableRepository.add(chatId, linkId);

        List<LinkResponse> linkResponseList = joinTableRepository.findAllByChatId(chatId);

        assertThat(linkResponseList).containsOnly(new LinkResponse(linkId, link));
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileFindingByTgChat() {
        Long chatId = 1L;
        Long linkId = 2L;

        Throwable ex = catchThrowable(() -> joinTableRepository.remove(chatId, linkId));

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
        chatRepository.add(firstChatId);
        Long firstLinkId = linkRepository.add(firstLink);
        chatRepository.add(secondChatId);
        Long secondLinkId = linkRepository.add(secondLink);
        joinTableRepository.add(firstChatId, firstLinkId);
        joinTableRepository.add(secondChatId, secondLinkId);

        List<JoinTableDto> linkResponseList = joinTableRepository.findAllByLinkId(secondLinkId);

        assertThat(linkResponseList).containsOnly(new JoinTableDto(secondChatId, secondLinkId));
    }
}
