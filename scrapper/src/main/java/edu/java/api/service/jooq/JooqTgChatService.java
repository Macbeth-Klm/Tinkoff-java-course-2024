package edu.java.api.service.jooq;

import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.service.TgChatService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JooqTgChatService implements TgChatService {
    private final JooqChatRepository jooqChatRepository;

    @Override
    public void register(Long tgChatId) {
        jooqChatRepository.add(tgChatId);
    }

    @Override
    public void unregister(Long tgChatId) {
        jooqChatRepository.remove(tgChatId);
    }
}
