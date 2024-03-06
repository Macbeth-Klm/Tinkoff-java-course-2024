package edu.java.bot.api.services;

import edu.java.exceptions.BadRequestException;
import edu.java.models.LinkUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Slf4j
@Service
public class BotService {
    public void postUpdate(LinkUpdate req, BindingResult errors) {
        if (errors.hasErrors()) {
            throw new BadRequestException(
                "Invalid HTTP-request parameters",
                "Некорректные параметры запроса"
            );
        }
        /*
        В дальнейшем будет реализована рассылка сообщений по всем чатам,
        пока решил поставить заглушку
         */
        log.info("Message has been send to user!");
    }
}
