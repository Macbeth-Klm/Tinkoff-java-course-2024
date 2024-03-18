package edu.java.api.domain.repository;

import java.util.List;

public interface ChatRepository {
    void add(Long chatId);

    void remove(Long chatId);

    List<Long> findAll();

    boolean isNotRegistered(Long id);
}
