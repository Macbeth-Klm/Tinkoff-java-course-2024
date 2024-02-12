package edu.java.bot.linkvalidators;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public final class LinkValidatorManager {
    private final List<LinkValidator> validators;

    public LinkValidatorManager(
        LinkValidator gitHubValidator,
        LinkValidator stackOverflowValidator
    ) {
        validators = List.of(
            gitHubValidator,
            stackOverflowValidator
        );
    }

    public boolean isValid(String link) {
        String scheme = "https://";
        if (!link.startsWith(scheme)) {
            link = scheme + link;
        }
        try {
            URI uri = new URI(link);
            for (var validator : validators) {
                if (validator.isValid(uri)) {
                    return true;
                }
            }
            return false;
        } catch (URISyntaxException ee) {
            return false;
        }
    }
}
