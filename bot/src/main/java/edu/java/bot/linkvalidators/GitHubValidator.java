package edu.java.bot.linkvalidators;

import java.net.URI;

public class GitHubValidator implements LinkValidator {
    @Override
    public boolean isValid(URI uri) {
        return uri.getHost().equals("github.com") && !uri.getPath().isEmpty();
    }
}
