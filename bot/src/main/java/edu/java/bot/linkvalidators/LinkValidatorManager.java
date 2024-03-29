package edu.java.bot.linkvalidators;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class LinkValidatorManager {
    private final List<LinkValidator> validators;

    public boolean isValid(String link) {
        String scheme = "https://";
        String fullUri = link;
        if (!link.startsWith(scheme)) {
            fullUri = scheme + link;
        }
        try {
            URI uri = new URI(fullUri);
            for (var validator : validators) {
                if (validator.isValid(uri)) {
                    return true;
                }
            }
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
