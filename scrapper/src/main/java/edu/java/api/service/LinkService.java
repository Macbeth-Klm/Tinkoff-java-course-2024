package edu.java.api.service;

import edu.java.model.LinkResponse;
import java.net.URI;
import java.util.List;

public interface LinkService {
    LinkResponse add(Long tgChatId, URI url);

    LinkResponse remove(Long tgChatId, URI url);

    List<LinkResponse> listAll(long tgChatId);
}
