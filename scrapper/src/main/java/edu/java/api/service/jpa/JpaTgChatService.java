package edu.java.api.service.jpa;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.service.TgChatService;
import edu.java.exception.NotFoundException;
import edu.java.model.domain.jpa.Chat;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaTgChatService implements TgChatService {
    private final JpaChatRepository jpaChatRepository;

    @Override
    @Transactional
    public void register(Long tgChatId) {
        if (jpaChatRepository.existsById(tgChatId)) {
            throw new DuplicateKeyException("Вы уже зарегистрированы!");
        }
        Chat chat = new Chat();
        chat.setId(tgChatId);
        jpaChatRepository.saveAndFlush(chat);
    }

    @Override
    @Transactional
    public void unregister(Long tgChatId) {
        if (!jpaChatRepository.existsById(tgChatId)) {
            throw new NotFoundException(
                "The user with the given chat id is not registered",
                "Пользователь не зарегистрирован"
            );
        }
        jpaChatRepository.deleteById(tgChatId);
        jpaChatRepository.flush();
    }
}
