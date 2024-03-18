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
    private final LinkRepository linkRepository;
    private final JoinTableRepository joinTableRepository;

    @Override
    public LinkResponse add(Long tgChatId, URI url) {
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                "The user with the given chat id is not registered",
                "Пользователь не зарегистрирован"
            );
        }
        Long linkId = (linkRepository.isExist(url)) ? linkRepository.findByUrl(url).id() : linkRepository.add(url);
        joinTableRepository.addRecord(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public LinkResponse remove(Long tgChatId, URI url) {
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                "The user with the given chat id is not registered",
                "Пользователь не зарегистрирован"
            );
        }
        Long linkId = linkRepository.findByUrl(url).id();
        joinTableRepository.deleteRecord(tgChatId, linkId);
        return new LinkResponse(linkId, url);
    }

    @Override
    public List<LinkResponse> listAll(long tgChatId) {
        if (chatRepository.isNotRegistered(tgChatId)) {
            throw new BadRequestException(
                "The user with the given chat id is not registered",
                "Пользователь не зарегистрирован"
            );
        }
        return joinTableRepository.findAllByChatId(tgChatId);
    }
}
