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
    @Value(value = "${api.github.defaultUrl}")
    private String defaultGitHubUrl;
    @Value(value = "${api.stackoverflow.defaultUrl}")
    private String defaultStackOverflowUrl;
    @Value(value = "${api.bot.defaultUrl}")
    private String defaultScrapperUrl;
    private final WebClient.Builder webClientBuilder = WebClient.builder();

    @Bean
    GitHubClient gitHubClient() {
        return new RegularGitHubClient(defaultGitHubUrl, webClientBuilder);
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        return new RegularStackOverflowClient(defaultStackOverflowUrl, webClientBuilder);
    }

    @Bean
    BotClient botClient() {
        return new BotClient(defaultScrapperUrl, webClientBuilder);
    }
}
