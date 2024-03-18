package edu.java.api.service.jooq;

import edu.java.api.domain.repository.ChatRepository;
import edu.java.api.domain.repository.JoinTableRepository;
import edu.java.api.domain.repository.LinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exceptions.BadRequestException;
import edu.java.models.LinkResponse;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JooqLinkService implements LinkService {
    private final ChatRepository jooqChatRepository;
    private final LinkRepository jooqLinkRepository;
    private final JoinTableRepository jooqJoinTableRepository;

    @Override
    public LinkResponse add(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        isValidUri(url);
        Long linkId = (jooqLinkRepository.isExist(url))
            ? jooqLinkRepository.findByUrl(url).id()
            : jooqLinkRepository.add(url);
        jooqJoinTableRepository.add(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public LinkResponse remove(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        Long linkId = jooqLinkRepository.findByUrl(url).id();
        jooqJoinTableRepository.remove(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public List<LinkResponse> listAll(long tgChatId) {
        registrationValidation(tgChatId);
        return jooqJoinTableRepository.findAllByChatId(tgChatId);
    }

    private void isValidUri(URI uri) {
        if (!uri.getHost().equals("github.com") && !uri.getHost().equals("stackoverflow.com")) {
            throw new BadRequestException(
                "That resources is not supported",
                "Данный ресурс не поддерживается!"
            );
        }
    }

    private void registrationValidation(Long tgChatId) {
        if (jooqChatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                "Пользователь не зарегистрирован",
                "The user with the given chat id is not registered"
            );
        }
    }
}
