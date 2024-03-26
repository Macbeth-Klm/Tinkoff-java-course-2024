package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.ChatRepository;
import edu.java.api.domain.repository.JoinTableRepository;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final ChatRepository jdbcChatRepository;
    private final LinkRepository jdbcLinkRepository;
    private final JoinTableRepository jdbcChatLinkRepository;

    @Override
    @Transactional
    public LinkResponse add(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        Long linkId;
        try {
            linkId = jdbcLinkRepository.findByUrl(url);
        } catch (EmptyResultDataAccessException ex) {
            linkId = jdbcLinkRepository.add(url);
        }
        jdbcChatLinkRepository.add(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    @Transactional
    public LinkResponse remove(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        try {
            Long linkId = jdbcLinkRepository.findByUrl(url);
            jdbcChatLinkRepository.remove(tgChatId, linkId);
            return new LinkResponse(linkId, url);
        } catch (EmptyResultDataAccessException ex) {
            throw new NotFoundException(
                "User with the given chat id is not tracking this link",
                "Вы не отслеживаете данную ссылку!"
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkResponse> listAll(long tgChatId) {
        registrationValidation(tgChatId);
        return jdbcChatLinkRepository.findAllByChatId(tgChatId);
    }

    private void registrationValidation(Long tgChatId) {
        if (!jdbcChatRepository.isRegistered(tgChatId)) {
            throw new NotFoundException(
                "The user with the given chat id is not registered",
                "Вы не зарегистрированы! Введите команду /start, чтобы зарегистрироваться."
            );
        }
    }
}
