package edu.java.bot.api.controller.http;

import edu.java.bot.api.service.BotService;
import edu.java.model.LinkUpdate;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/updates")
public class BotController {
    private final BotService botService;

    @Autowired
    public BotController(@Qualifier("regularBotService") BotService botService) {
        this.botService = botService;
    }

    @PostMapping
    public ResponseEntity<Void> postUpdate(@RequestBody @Valid LinkUpdate req) {
        botService.postUpdate(req);
        return ResponseEntity
            .ok()
            .build();
    }
}
