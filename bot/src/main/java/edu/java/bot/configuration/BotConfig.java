package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.commands.Command;
import edu.java.bot.commands.HelpCommand;
import edu.java.bot.commands.ListCommand;
import edu.java.bot.commands.StartCommand;
import edu.java.bot.commands.TrackCommand;
import edu.java.bot.commands.UntrackCommand;
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
    Command trackCommand(ScrapperClient scrapperClient) {
        return new TrackCommand(scrapperClient);
    }

    @Bean
    Command untrackCommand(ScrapperClient scrapperClient) {
        return new UntrackCommand(scrapperClient);
    }

    @Bean
    Command helpCommand(List<Command> commands) {
        List<String> otherCommands = commands.stream()
            .map(command -> command.name() + " - " + command.description() + "\n").toList();
        return new HelpCommand(otherCommands);
    }
}
