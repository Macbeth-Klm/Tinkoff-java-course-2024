package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
import edu.java.bot.linkvalidators.GitHubValidator;
import edu.java.bot.linkvalidators.LinkValidator;
import edu.java.bot.linkvalidators.LinkValidatorManager;
import edu.java.bot.linkvalidators.StackOverflowValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BotConfig {
    @Bean
    TelegramBot telegramBot(ApplicationConfig applicationConfig) {
        return new TelegramBot(applicationConfig.telegramToken());
    }

    @Bean
    LinkValidator gitHubValidator() {
        return new GitHubValidator();
    }

    @Bean
    LinkValidator stackOverflowValidator() {
        return new StackOverflowValidator();
    }

    @Bean LinkValidatorManager linkValidatorManager(List<LinkValidator> validators) {
        return new LinkValidatorManager(validators);
    }

    @Bean
    ScrapperClient scrapperClient(
        @Value(value = "${api.scrapper.defaultUrl}") String defaultScrapperUrl,
        WebClient.Builder webClientBuilder
    ) {
        return new ScrapperClient(defaultScrapperUrl, webClientBuilder);
    }

    @Bean
    Command startCommand(ScrapperClient scrapperClient) {
        return new StartCommand(scrapperClient);
    }

    @Bean
    Command listCommand(ScrapperClient scrapperClient) {
        return new ListCommand(scrapperClient);
    }

    @Bean
    Command trackCommand(LinkValidatorManager validatorManager, ScrapperClient scrapperClient) {
        return new TrackCommand(validatorManager, scrapperClient);
    }

    @Bean
    Command untrackCommand(LinkValidatorManager validatorManager, ScrapperClient scrapperClient) {
        return new UntrackCommand(validatorManager, scrapperClient);
    }

    @Bean
    Command helpCommand(List<Command> commands) {
        List<String> otherCommands = commands.stream()
            .map(command -> command.name() + " - " + command.description() + "\n").toList();
        return new HelpCommand(otherCommands);
    }
}
