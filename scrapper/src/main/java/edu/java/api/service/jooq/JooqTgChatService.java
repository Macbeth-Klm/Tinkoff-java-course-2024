package edu.java.api.service.jooq;

import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JooqTgChatService implements TgChatService {
    private final JooqChatRepository jooqChatRepository;

    @Override
    @Transactional
    public void register(Long tgChatId) {
        jooqChatRepository.add(tgChatId);
    }

    @Override
    @Transactional
    public void unregister(Long tgChatId) {
        jooqChatRepository.remove(tgChatId);
    }
}
