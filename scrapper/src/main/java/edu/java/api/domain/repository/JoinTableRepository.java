package edu.java.api.domain.repository;

import edu.java.api.domain.dto.JoinTableDto;
import edu.java.models.LinkResponse;
import java.util.List;

public interface JoinTableRepository {
    void add(Long chatId, Long linkId);

    void remove(Long chatId, Long linkId);

    List<JoinTableDto> findAll();

    List<LinkResponse> findAllByChatId(Long chatId);

    List<JoinTableDto> findAllByLinkId(Long linkId);

}
