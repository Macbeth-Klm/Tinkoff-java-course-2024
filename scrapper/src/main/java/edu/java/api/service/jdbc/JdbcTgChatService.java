package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.service.TgChatService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final JdbcChatRepository jdbcChatRepository;

    @Override
    public void register(Long tgChatId) {
        jdbcChatRepository.add(tgChatId);
    }

    @Override
    public void unregister(Long tgChatId) {
        jdbcChatRepository.remove(tgChatId);
    }
}
