package edu.java.configuration;

import edu.java.clients.BotClient.BotClient;
import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.clients.GitHubClient.RegularGitHubClient;
import edu.java.clients.StackOverflowClient.RegularStackOverflowClient;
import edu.java.clients.StackOverflowClient.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfiguration {
    @Bean
    GitHubClient gitHubClient(
        @Value(value = "${api.github.defaultUrl}") String defaultGitHubUrl,
        WebClient.Builder webClientBuilder
    ) {
        return new RegularGitHubClient(defaultGitHubUrl, webClientBuilder);
    }

    @Bean
    StackOverflowClient stackOverflowClient(
        @Value(value = "${api.stackoverflow.defaultUrl}") String defaultStackOverflowUrl,
        WebClient.Builder webClientBuilder
    ) {
        return new RegularStackOverflowClient(defaultStackOverflowUrl, webClientBuilder);
    }

    @Bean
    BotClient botClient(
        @Value(value = "${api.bot.defaultUrl}") String defaultScrapperUrl,
        WebClient.Builder webClientBuilder
    ) {
        return new BotClient(defaultScrapperUrl, webClientBuilder);
    }
}
