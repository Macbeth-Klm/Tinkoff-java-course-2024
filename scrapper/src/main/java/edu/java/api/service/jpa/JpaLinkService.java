package edu.java.api.service.jpa;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exceptions.BadRequestException;
import edu.java.exceptions.NotFoundException;
import edu.java.models.LinkResponse;
import edu.java.models.jpa.Chat;
import edu.java.models.jpa.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaChatRepository jpaChatRepository;
    private final JpaLinkRepository jpaLinkRepository;
    private final String registrationExceptionMessage = "The user with the given chat id is not registered";
    private final String registrationExceptionDescription = "Пользователь уже зарегистрирован";

    @Override
    @Transactional
    public LinkResponse add(Long tgChatId, URI url) {
        isValidUri(url);
        String urlToString = url.toString();
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new BadRequestException(
                    registrationExceptionMessage,
                    registrationExceptionDescription
                )
            );
        var links = chat.getLinks().stream().map(Link::getUrl).toList();
        if (links.contains(urlToString)) {
            throw new BadRequestException(
                "User with the given chat id is already tracking this link",
                "Пользователь уже отслеживает данную ссылку"
            );
        }

        Link link = jpaLinkRepository.findLinkByUrl(urlToString).orElseGet(
            () -> {
                Link l = new Link();
                l.setUrl(urlToString);
                l.setUpdatedAt(OffsetDateTime.now());
                l.setCheckedAt(OffsetDateTime.now());
                l.setChats(new HashSet<>());
                return jpaLinkRepository.saveAndFlush(l);
            }
        );
        chat.addLink(link);
        jpaChatRepository.saveAndFlush(chat);
        link = jpaLinkRepository.saveAndFlush(link);
        return new LinkResponse(
            link.getId(),
            url
        );
    }

    @Override
    @Transactional
    public LinkResponse remove(Long tgChatId, URI url) {
        isValidUri(url);
        String urlToString = url.toString();
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new BadRequestException(
                    registrationExceptionMessage,
                    registrationExceptionDescription
                )
            );
        var links = chat.getLinks().stream().map(Link::getUrl).toList();
        if (!links.contains(urlToString)) {
            throw new NotFoundException(
                "User with the given chat id is not tracking this link",
                "Пользователь не отслеживает данную ссылку"
            );
        }

        Link link = jpaLinkRepository.findLinkByUrl(urlToString).get();
        chat.removeLink(link);
        jpaChatRepository.saveAndFlush(chat);
        link = jpaLinkRepository.saveAndFlush(link);
        return new LinkResponse(
            link.getId(),
            url
        );
    }

    @Override
    @Transactional
    public List<LinkResponse> listAll(long tgChatId) {
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new BadRequestException(
                    registrationExceptionMessage,
                    registrationExceptionDescription
                )
            );
        return chat.getLinks().stream()
            .map(link -> new LinkResponse(link.getId(), URI.create(link.getUrl()))).toList();
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
