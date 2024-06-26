package edu.java.bot.configuration.bot;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.client.ScrapperClient;
import edu.java.bot.command.Command;
import edu.java.bot.command.HelpCommand;
import edu.java.bot.command.ListCommand;
import edu.java.bot.command.StartCommand;
import edu.java.bot.command.TrackCommand;
import edu.java.bot.command.UntrackCommand;
import edu.java.bot.configuration.ApplicationConfig;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class BotConfig {
    private final MeterRegistry registry;

    @Bean
    TelegramBot telegramBot(ApplicationConfig applicationConfig) {
        return new TelegramBot(applicationConfig.telegramToken());
    }

    @Bean Counter counter() {
        return Counter.builder("messages.processed")
            .description("Processed messages count")
            .register(registry);
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
