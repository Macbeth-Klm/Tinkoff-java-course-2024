package edu.java.model;

import java.net.URI;

public record LinkResponse(
    Long id,
    URI url
) {
}
