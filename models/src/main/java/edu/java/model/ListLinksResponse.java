package edu.java.model;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {
}
