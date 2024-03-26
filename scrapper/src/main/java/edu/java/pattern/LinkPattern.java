package edu.java.pattern;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LinkPattern {
    GITHUB("https://github\\.com/[a-zA-Z0-9-]+/[a-zA-Z0-9-]+"),
    STACKOVERFLOW("https://stackoverflow\\.com/questions/\\d+");

    private final String regex;
}
