package edu.java.api.models;

import jakarta.validation.constraints.NotEmpty;

public record RemoveLinkRequest(@NotEmpty String link) {
}
