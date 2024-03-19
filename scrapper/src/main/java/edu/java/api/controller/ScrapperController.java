package edu.java.api.controller;

import edu.java.api.service.LinkService;
import edu.java.api.service.TgChatService;
import edu.java.exceptions.BadRequestException;
import edu.java.models.AddLinkRequest;
import edu.java.models.LinkResponse;
import edu.java.models.ListLinksResponse;
import edu.java.models.RemoveLinkRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    private final TgChatService jdbcTgChatService;
    private final LinkService jdbcLinkService;
    private final String exceptionName = "Invalid HTTP-request parameters";
    private final String exceptionDescription = "Некорректные параметры запроса";

    @PostMapping("/tg-chat/{id}")
    public ResponseEntity<Void> registerChat(@PathVariable("id") Long id) {
        checkValidationId(id);
        jdbcTgChatService.register(id);
        return ResponseEntity
            .ok()
            .build();
    }

    @DeleteMapping("/tg-chat/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable("id") Long id) {
        checkValidationId(id);
        jdbcTgChatService.unregister(id);
        return ResponseEntity
            .ok()
            .build();
    }

    @GetMapping("/links")
    public ResponseEntity<ListLinksResponse> getLinks(@RequestHeader("Tg-Chat-Id") Long id) {
        checkValidationId(id);
        List<LinkResponse> linkResponseList = jdbcLinkService.listAll(id);
        ListLinksResponse response = new ListLinksResponse(linkResponseList, linkResponseList.size());
        return ResponseEntity
            .ok()
            .body(response);
    }

    @PostMapping("/links")
    public ResponseEntity<LinkResponse> addLinks(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody @Valid AddLinkRequest req,
        BindingResult errors
    ) {
        if (id < 1L || errors.hasErrors()) {
            throw new BadRequestException(
                exceptionName,
                exceptionDescription
            );
        }
        URI link = req.link();
        LinkResponse response = jdbcLinkService.add(id, req.link());
        return ResponseEntity
            .ok()
            .body(response);
    }

    @DeleteMapping("/links")
    public ResponseEntity<LinkResponse> deleteLinks(
        @RequestHeader("Tg-Chat-Id") Long id,
        @RequestBody @Valid RemoveLinkRequest req,
        BindingResult errors
    ) {
        if (id < 1L || errors.hasErrors()) {
            throw new BadRequestException(
                exceptionName,
                exceptionDescription
            );
        }
        URI link = req.link();
        LinkResponse response = jdbcLinkService.remove(id, link);
        return ResponseEntity
            .ok()
            .body(response);
    }

    private void checkValidationId(Long id) {
        if (id < 1L) {
            throw new BadRequestException(
                exceptionName,
                exceptionDescription
            );
        }
    }
}
