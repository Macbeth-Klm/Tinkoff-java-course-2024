package edu.java.api.service.jdbc;

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
public class JdbcLinkService implements LinkService {
    private final ChatRepository chatRepository;
    private final LinkRepository jdbcLinkRepository;
    private final JoinTableRepository jdbcJoinTableRepository;

    @Override
    public LinkResponse add(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        isValidUri(url);
        Long linkId = (jdbcLinkRepository.isExist(url))
            ? jdbcLinkRepository.findByUrl(url).id()
            : jdbcLinkRepository.add(url);
        jdbcJoinTableRepository.add(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public LinkResponse remove(Long tgChatId, URI url) {
        registrationValidation(tgChatId);
        Long linkId = jdbcLinkRepository.findByUrl(url).id();
        jdbcJoinTableRepository.remove(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public List<LinkResponse> listAll(long tgChatId) {
        registrationValidation(tgChatId);
        return jdbcJoinTableRepository.findAllByChatId(tgChatId);
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
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                "The user with the given chat id is not registered",
                "Пользователь не зарегистрирован"
            );
        }
    }
}
