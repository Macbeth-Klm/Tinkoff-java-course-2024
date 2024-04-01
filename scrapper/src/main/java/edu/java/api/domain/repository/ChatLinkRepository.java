package edu.java.api.domain.repository;

import edu.java.api.domain.dto.ChatLinkDto;
import edu.java.models.LinkResponse;
import java.util.List;

public interface ChatLinkRepository {
    void add(Long chatId, Long linkId);

    void remove(Long chatId, Long linkId);

    List<ChatLinkDto> findAll();

    List<LinkResponse> findAllByChatId(Long chatId);

    List<ChatLinkDto> findAllByLinkId(Long linkId);

}
