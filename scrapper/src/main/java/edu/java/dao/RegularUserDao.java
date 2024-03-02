package edu.java.dao;

import edu.java.api.exceptions.ScrapperInvalidReqException;
import edu.java.api.exceptions.ScrapperNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RegularUserDao implements UserDao {
    private final Map<Long, Map<Long, String>> users;

    public RegularUserDao() {
        users = new ConcurrentHashMap<>();
    }

    @Override
    public void registerUser(Long id) {
        if (users.containsKey(id)) {
            throw new ScrapperInvalidReqException(
                "User with the given chat id is already registered",
                "Пользователь уже зарегистрирован"
            );
        }
        users.put(id, new HashMap<>());
    }

    @Override
    public void deleteUserById(Long id) {
        notFoundCheck(id);
        users.remove(id);
    }

    @Override
    public Map<Long, String> getLinks(Long id) {
        notFoundCheck(id);
        return users.get(id);
    }

    @Override
    public Long addLink(Long id, String link) {
        notFoundCheck(id);
        var links = users.get(id);
        if (links.containsValue(link)) {
            throw new ScrapperInvalidReqException(
                "User with the given chat id is already tracking this link",
                "Пользователь уже отслеживает данную ссылку"
            );
        }
        links.put(links.size() + 1L, link);
        return links.size() + 1L;
    }

    @Override
    public Long deleteLink(Long id, String link) {
        notFoundCheck(id);
        var links = users.get(id);
        for (var linkId : links.keySet()) {
            if (links.get(linkId).equals(link)) {
                links.remove(linkId);
                return linkId;
            }
        }
        throw new ScrapperNotFoundException(
            "The user with the given chat id is not tracking this link",
            "Ссылка не найдена"
        );
    }

    private void notFoundCheck(Long id) {
        if (!users.containsKey(id)) {
            throw new ScrapperNotFoundException(
                "User with the given chat id is not exist",
                "Чат не существует"
            );
        }
    }
}
