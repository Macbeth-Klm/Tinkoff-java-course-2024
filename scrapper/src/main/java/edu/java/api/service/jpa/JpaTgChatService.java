package edu.java.api.service.jpa;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.service.TgChatService;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.jpa.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaTgChatService implements TgChatService {
    private final JpaChatRepository jpaChatRepository;

    @Override
    @Transactional
    public void register(Long tgChatId) {
        if (jpaChatRepository.existsById(tgChatId)) {
            throw new BadRequestException(
                "User with the given chat id is already registered",
                "Пользователь уже зарегистрирован"
            );
        }
        Chat chat = new Chat();
        chat.setId(tgChatId);
        jpaChatRepository.saveAndFlush(chat);
    }

    @Override
    @Transactional
    public void unregister(Long tgChatId) {
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new NotFoundException(
                    "The user with the given chat id is not registered",
                    "Пользователь не зарегистрирован"
                )
            );
        jpaChatRepository.deleteById(tgChatId);
        jpaChatRepository.flush();
    }
}
