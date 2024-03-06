package edu.java.models;

import java.net.URI;

public record LinkResponse(
    Long id,
    URI url
) {
}
