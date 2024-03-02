package edu.java.api.controllers;

import edu.java.api.models.LinkUpdate;
import edu.java.api.services.BotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
@RequiredArgsConstructor
public class BotController {
    private final BotService botService;

    @PostMapping
    public ResponseEntity<Void> postUpdate(@RequestBody @Valid LinkUpdate req, BindingResult errors) {
        botService.postUpdate(req, errors);
        return ResponseEntity
            .ok()
            .header("Description", "Обновление обработано")
            .build();
    }
}
