package edu.java.bot.linkvalidators;

import java.net.URI;

public interface LinkValidator {
    boolean isValid(URI uri);
}
