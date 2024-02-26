package edu.java.bot.linkvalidators;

import java.net.URI;

public class StackOverflowValidator implements LinkValidator {
    @Override
    public boolean isValid(URI uri) {
        return uri.getHost().equals("stackoverflow.com") && !uri.getPath().isEmpty();
    }
}
