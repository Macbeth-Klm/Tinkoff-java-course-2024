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
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChatRepositoryTest extends IntegrationTest {
    @Autowired
    private ChatRepository chatRepository;

    @Test
    @Transactional
    @Rollback
    void shouldAddChat() {
        Long chatId = 1L;

        chatRepository.add(chatId);
        List<Long> chatIds = chatRepository.findAll();

        assertThat(chatIds).containsOnly(chatId);
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowDuplicateKeyExceptionWhileAddingChat() {
        Long chatId = 1L;

        chatRepository.add(chatId);

        Throwable ex = catchThrowable(() -> chatRepository.add(chatId));

        assertThat(ex).isInstanceOf(BadRequestException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldRemoveChat() {
        Long chatId = 1L;

        chatRepository.add(chatId);
        chatRepository.remove(chatId);
        List<Long> chatIds = chatRepository.findAll();

        assertThat(chatIds).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void shouldThrowNotFoundExceptionWhileRemovingChat() {
        Long chatId = 1L;

        Throwable ex = catchThrowable(() -> chatRepository.remove(chatId));

        assertThat(ex).isInstanceOf(NotFoundException.class);
    }

    @Test
    @Transactional
    @Rollback
    void shouldReturnCorrectResultForIsRegisteredChat() {
        Long firstChatId = 1L;
        Long secondChatId = 2L;

        chatRepository.add(firstChatId);
        boolean firstIsNotRegistered = chatRepository.isNotRegistered(firstChatId);
        boolean secondIsNotRegistered = chatRepository.isNotRegistered(secondChatId);

        assertAll(
            () -> assertFalse(firstIsNotRegistered),
            () -> assertTrue(secondIsNotRegistered)
        );
    }
}
