package edu.java.scrapper.domain;

import edu.java.api.domain.repository.ChatRepository;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.scrapper.IntegrationTest;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JdbcChatRepositoryTest extends IntegrationTest {
    @Autowired
    private ChatRepository jdbcChatRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddChat() {
        Long chatId = 1L;

        jdbcChatRepository.add(chatId);
        List<Long> chatIds = jdbcChatRepository.findAll();

        assertThat(chatIds).containsOnly(chatId);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingChat() {
        Long chatId = 1L;

        jdbcChatRepository.add(chatId);

        Throwable ex = catchThrowable(() -> jdbcChatRepository.add(chatId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveChat() {
        Long chatId = 1L;

        jdbcChatRepository.add(chatId);
        jdbcChatRepository.remove(chatId);
        List<Long> chatIds = jdbcChatRepository.findAll();

        assertThat(chatIds).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingChat() {
        Long chatId = 1L;

        Throwable ex = catchThrowable(() -> jdbcChatRepository.remove(chatId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCorrectResultForIsRegisteredChat() {
        Long firstChatId = 1L;
        Long secondChatId = 2L;

        jdbcChatRepository.add(firstChatId);
        boolean firstIsNotRegistered = jdbcChatRepository.isNotRegistered(firstChatId);
        boolean secondIsNotRegistered = jdbcChatRepository.isNotRegistered(secondChatId);

        assertAll(
            () -> assertFalse(firstIsNotRegistered),
            () -> assertTrue(secondIsNotRegistered)
        );
    }
}
