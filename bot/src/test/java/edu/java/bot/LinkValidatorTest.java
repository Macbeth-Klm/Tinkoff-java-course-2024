package edu.java.bot;

import edu.java.bot.linkvalidators.GitHubValidator;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import edu.java.bot.linkvalidators.StackOverflowValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.List;

public class LinkValidatorTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "https://github.com/pengrad/java-telegram-bot-api",
        "github.com/pengrad/java-telegram-bot-api",
        "https://stackoverflow.com/questions/292357",
        "stackoverflow.com/questions/292357"
    })
    void shouldHandleCorrectURI(String uri) {
        LinkValidatorManager validator = new LinkValidatorManager(
            List.of(
                new GitHubValidator(),
                new StackOverflowValidator()
            )
        );

        Assertions.assertTrue(validator.isValid(uri));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "https://github.com",
        "github.com",
        "https://stackoverflow.com",
        "stackoverflow.com",
        "idk who can type uri like this but everything is possible :D",
        "https://github.com/peng#%^$rad/java-telegram-bot-api"
    })
    void shouldHandleIncorrectURI(String uri) {
        LinkValidatorManager validator = new LinkValidatorManager(
            List.of(
                new GitHubValidator(),
                new StackOverflowValidator()
            )
        );

        Assertions.assertFalse(validator.isValid(uri));
    }
}
