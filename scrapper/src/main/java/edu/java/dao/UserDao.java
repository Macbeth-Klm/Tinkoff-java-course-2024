package edu.java.dao;

import java.net.URI;
import java.util.Map;

public interface UserDao {
    void registerUser(Long id);

    void deleteUserById(Long id);

    Map<Long, URI> getLinks(Long id);

    Long addLink(Long id, URI link);

    Long deleteLink(Long id, URI link);

}
