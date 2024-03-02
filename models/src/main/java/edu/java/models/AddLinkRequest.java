package edu.java.models;

import jakarta.validation.constraints.NotEmpty;

public record AddLinkRequest(@NotEmpty String link) {
}
