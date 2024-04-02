package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatLinkRepository;
import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exception.NotFoundException;
import edu.java.model.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {
    private final JdbcChatRepository jdbcChatRepository;
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcChatLinkRepository jdbcChatLinkRepository;

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
