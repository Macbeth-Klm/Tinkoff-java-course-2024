package edu.java.api.service.jooq;

import edu.java.api.domain.repository.ChatRepository;
import edu.java.api.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JooqTgChatService implements TgChatService {
    private final ChatRepository jooqChatRepository;

    @Override
    public void register(Long tgChatId) {
        jooqChatRepository.add(tgChatId);
    }

    @Override
    public void unregister(Long tgChatId) {
        jooqChatRepository.remove(tgChatId);
    }
}
