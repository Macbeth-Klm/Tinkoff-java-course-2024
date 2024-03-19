package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.ChatRepository;
import edu.java.api.service.TgChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {
    private final ChatRepository jdbcChatRepository;

    @Override
    public void register(Long tgChatId) {
        jdbcChatRepository.add(tgChatId);
    }

    @Override
    public void unregister(Long tgChatId) {
        jdbcChatRepository.remove(tgChatId);
    }
}
