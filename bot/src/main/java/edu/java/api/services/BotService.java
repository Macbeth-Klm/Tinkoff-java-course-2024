package edu.java.api.services;

import edu.java.api.exceptions.BotApiException;
import edu.java.api.models.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
public class BotService {
    public void postUpdate(LinkUpdate req, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new BotApiException("Invalid HTTP-request parameters");
        }
        /*
        В дальнейшем будет реализована рассылка сообщений по всем чатам,
        но пока тут будет заглушка
         */
        log.info("Message has been send to user!");
    }
}
