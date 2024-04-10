package edu.java.api.service.jpa;

import edu.java.api.domain.repository.jpa.JpaChatRepository;
import edu.java.api.domain.repository.jpa.JpaLinkRepository;
import edu.java.api.service.LinkService;
import edu.java.exception.BadRequestException;
import edu.java.exception.NotFoundException;
import edu.java.model.LinkResponse;
import edu.java.model.domain.jpa.Chat;
import edu.java.model.domain.jpa.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {
    private final JpaChatRepository jpaChatRepository;
    private final JpaLinkRepository jpaLinkRepository;
    private final String registrationExceptionMessage = "The user with the given chat id is not registered";
    private final String registrationExceptionDescription =
        "Вы не зарегистрированы! Введите команду /start, чтобы зарегистрироваться.";

    @Override
    @Transactional
    public LinkResponse add(Long tgChatId, URI url) {
        String urlToString = url.toString();
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new NotFoundException(
                    registrationExceptionMessage,
                    registrationExceptionDescription
                )
            );
        List<URI> links = chat.getLinks().stream().map(Link::getUrl).toList();
        if (links.contains(url)) {
            throw new DuplicateKeyException("Вы уже отслеживаете данный ресурс!");
        }

        Link link = (Link) jpaLinkRepository.findLinkByUrl(urlToString).orElseGet(
            () -> {
                Link l = new Link();
                l.setUrl(url);
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
        String urlToString = url.toString();
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new BadRequestException(
                    registrationExceptionMessage,
                    registrationExceptionDescription
                )
            );
        List<URI> links = chat.getLinks().stream().map(Link::getUrl).toList();
        if (!links.contains(url)) {
            throw new NotFoundException(
                "User with the given chat id is not tracking this link",
                "Пользователь не отслеживает данную ссылку"
            );
        }

        Link link = (Link) jpaLinkRepository.findLinkByUrl(urlToString).get();
        chat.removeLink(link);
        jpaChatRepository.saveAndFlush(chat);
        link = jpaLinkRepository.saveAndFlush(link);
        return new LinkResponse(
            link.getId(),
            url
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkResponse> listAll(long tgChatId) {
        Chat chat = jpaChatRepository.findById(tgChatId)
            .orElseThrow(
                () -> new BadRequestException(
                    registrationExceptionMessage,
                    registrationExceptionDescription
                )
            );
        return chat.getLinks().stream()
            .map(link -> new LinkResponse(link.getId(), link.getUrl())).toList();
    }
}
