package edu.java.models;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {
}
