package edu.java.api.services;

import edu.java.dao.UserDao;
import edu.java.models.LinkResponse;
import edu.java.models.ListLinksResponse;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ScrapperService {

    private final UserDao userDao;

    public ScrapperService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void registerChat(Long id) {
        userDao.registerUser(id);
    }

    public void deleteChat(Long id) {
        userDao.deleteUserById(id);
    }

    public ListLinksResponse getLinks(Long id) {
        Map<Long, URI> responses = userDao.getLinks(id);
        List<LinkResponse> linkResponseList = responses.entrySet().stream()
            .map(entry -> new LinkResponse(entry.getKey(), entry.getValue())).toList();
        return new ListLinksResponse(linkResponseList, linkResponseList.size());
    }

    public LinkResponse addLinks(Long id, URI link) {
        var linkId = userDao.addLink(id, link);
        return new LinkResponse(linkId, link);
    }

    public LinkResponse deleteLinks(Long id, URI link) {
        var linkId = userDao.deleteLink(id, link);
        return new LinkResponse(linkId, link);
    }
}
