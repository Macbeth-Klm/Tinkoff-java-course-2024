package edu.java.api.service.jooq;

import edu.java.api.domain.repository.jooq.JooqChatLinkRepository;
import edu.java.api.domain.repository.jooq.JooqChatRepository;
import edu.java.api.domain.repository.jooq.JooqLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exception.NotFoundException;
import edu.java.model.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JooqLinkService implements LinkService {
    private final JooqChatRepository jooqChatRepository;
    private final JooqLinkRepository jooqLinkRepository;
    private final JooqChatLinkRepository jooqChatLinkRepository;

    @Override
    @Transactional
    public LinkResponse add(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        Long linkId;
        try {
            linkId = jooqLinkRepository.findByUrl(url);
        } catch (NullPointerException ex) {
            linkId = jooqLinkRepository.add(url);
        }
        jooqChatLinkRepository.add(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    @Transactional
    public LinkResponse remove(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        try {
            Long linkId = jooqLinkRepository.findByUrl(url);
            jooqChatLinkRepository.remove(tgChatId, linkId);
            return new LinkResponse(linkId, url);
        } catch (NullPointerException ex) {
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
        return jooqChatLinkRepository.findAllByChatId(tgChatId);
    }

    private void registrationValidation(Long tgChatId) {
        if (!jooqChatRepository.isRegistered(tgChatId)) {
            throw new NotFoundException(
                "The user with the given chat id is not registered",
                "Вы не зарегистрированы! Введите команду /start, чтобы зарегистрироваться."
            );
        }
    }
}
