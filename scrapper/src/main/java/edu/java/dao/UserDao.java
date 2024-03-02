package edu.java.dao;

import java.util.Map;

public interface UserDao {
    void registerUser(Long id);

    void deleteUserById(Long id);

    Map<Long, String> getLinks(Long id);

    Long addLink(Long id, String link);

    Long deleteLink(Long id, String link);

}
