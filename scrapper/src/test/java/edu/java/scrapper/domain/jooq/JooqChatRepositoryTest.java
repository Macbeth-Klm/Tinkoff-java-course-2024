package edu.java.scrapper.domain.jooq;

import edu.java.api.domain.repository.jooq.JooqChatRepository;
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
public class JooqChatRepositoryTest extends IntegrationTest {
    @Autowired
    private JooqChatRepository jooqChatRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddChat() {
        Long chatId = 1L;

        jooqChatRepository.add(chatId);
        List<Long> chatIds = jooqChatRepository.findAll();

        assertThat(chatIds).containsOnly(chatId);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingChat() {
        Long chatId = 1L;

        jooqChatRepository.add(chatId);

        Throwable ex = catchThrowable(() -> jooqChatRepository.add(chatId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveChat() {
        Long chatId = 1L;

        jooqChatRepository.add(chatId);
        jooqChatRepository.remove(chatId);
        List<Long> chatIds = jooqChatRepository.findAll();

        assertThat(chatIds).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingChat() {
        Long chatId = 1L;

        Throwable ex = catchThrowable(() -> jooqChatRepository.remove(chatId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCorrectResultForIsRegisteredChat() {
        Long firstChatId = 1L;
        Long secondChatId = 2L;

        jooqChatRepository.add(firstChatId);
        boolean firstIsNotRegistered = jooqChatRepository.isNotRegistered(firstChatId);
        boolean secondIsNotRegistered = jooqChatRepository.isNotRegistered(secondChatId);

        assertAll(
            () -> assertFalse(firstIsNotRegistered),
            () -> assertTrue(secondIsNotRegistered)
        );
    }
}
