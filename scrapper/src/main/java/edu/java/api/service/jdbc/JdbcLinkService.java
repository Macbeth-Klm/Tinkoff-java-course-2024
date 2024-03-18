package edu.java.api.service.jdbc;

import edu.java.api.domain.repository.jdbc.JdbcChatRepository;
import edu.java.api.domain.repository.jdbc.JdbcJoinTableRepository;
import edu.java.api.domain.repository.jdbc.JdbcLinkRepository;
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
    private final JdbcChatRepository chatRepository;
    private final JdbcLinkRepository jdbcLinkRepository;
    private final JdbcJoinTableRepository jdbcJoinTableRepository;
    private final String userIsNotRegisteredMessage = "The user with the given chat id is not registered";
    private final String userIsNotRegisteredDescription = "Пользователь не зарегистрирован";

    @Override
    public LinkResponse add(Long tgChatId, URI url) {
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                userIsNotRegisteredMessage,
                userIsNotRegisteredDescription
            );
        }
        isValidUri(url);
        Long linkId = (jdbcLinkRepository.isExist(url))
            ? jdbcLinkRepository.findByUrl(url).id()
            : jdbcLinkRepository.add(url);
        jdbcJoinTableRepository.add(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public LinkResponse remove(Long tgChatId, URI url) {
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                userIsNotRegisteredMessage,
                userIsNotRegisteredDescription
            );
        }
        Long linkId = jdbcLinkRepository.findByUrl(url).id();
        jdbcJoinTableRepository.remove(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public List<LinkResponse> listAll(long tgChatId) {
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                userIsNotRegisteredMessage,
                userIsNotRegisteredDescription
            );
        }
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
}
