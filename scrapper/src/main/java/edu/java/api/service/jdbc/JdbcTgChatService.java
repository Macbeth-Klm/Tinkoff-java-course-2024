package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final JdbcChatRepository jdbcChatRepository;

    @Override
    @Transactional
    public void register(Long tgChatId) {
        jdbcChatRepository.add(tgChatId);
    }

    @Override
    @Transactional
    public void unregister(Long tgChatId) {
        jdbcChatRepository.remove(tgChatId);
    }
}
