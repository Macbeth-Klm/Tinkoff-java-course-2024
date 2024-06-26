package edu.java.api.controller;

import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.exception.BadRequestException;
import edu.java.model.AddLinkRequest;
import edu.java.model.LinkResponse;
import edu.java.model.ListLinksResponse;
import edu.java.model.RemoveLinkRequest;
import edu.java.pattern.LinkPattern;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ScrapperController {
    private final TgChatService tgChatService;
    private final LinkService linkService;
    private static final String BAD_REQ_EXCEPTION_NAME = "Invalid HTTP-request parameters";
    private static final String BAD_REQ_EXCEPTION_DESCRIPTION = "Некорректные параметры запроса";

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable("id") Long id) {
        idValidation(id);
        try {
            tgChatService.register(id);
        } catch (DuplicateKeyException ex) {
            throw new BadRequestException(
                "The user is already registered",
                "Вы уже зарегистрированы!"
            );
        }
        return ResponseEntity
            .ok()
            .build();
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long id) {
        idValidation(id);
        tgChatService.unregister(id);
        return ResponseEntity
            .ok()
            .build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        idValidation(id);
        List<LinkResponse> linkResponseList = linkService.listAll(id);
        ListLinksResponse response = new ListLinksResponse(linkResponseList, linkResponseList.size());
        return ResponseEntity
            .ok()
            .body(response);
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLinks(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody AddLinkRequest req
    ) {
        idValidation(id);
        URI link = req.link();
        urlValidation(link);
        try {
            LinkResponse response = linkService.add(id, link);
            return ResponseEntity
                .ok()
                .body(response);
        } catch (DuplicateKeyException ex) {
            throw new BadRequestException(
                "The user is already tracking this link",
                "Вы уже отслеживаете данный ресурс!"
            );
        }
    }

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> deleteLinks(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody RemoveLinkRequest req
    ) {
        idValidation(id);
        URI link = req.link();
        urlValidation(link);
        LinkResponse response = linkService.remove(id, link);
        return ResponseEntity
            .ok()
            .body(response);
    }

    private void idValidation(Long id) {
        if (id < 1L) {
            throw new BadRequestException(
                BAD_REQ_EXCEPTION_NAME,
                BAD_REQ_EXCEPTION_DESCRIPTION
            );
        }
    }

    private void urlValidation(URI uri) {
        String uriString = uri.toString();
        if (uriString.isEmpty()) {
            throw new BadRequestException(
                BAD_REQ_EXCEPTION_NAME,
                BAD_REQ_EXCEPTION_DESCRIPTION
            );
        }
        for (LinkPattern pattern : LinkPattern.values()) {
            if (uriString.matches(pattern.getRegex())) {
                return;
            }
        }
        throw new BadRequestException(
            "Given url is not supported by that service",
            "Данный ресурс не поддерживается сервисом!"
        );
    }
}
