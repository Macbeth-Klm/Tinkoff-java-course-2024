package edu.java.configuration;

import edu.java.clients.GitHubClient.GitHubClient;
import edu.java.clients.GitHubClient.RegularGitHubClient;
import edu.java.clients.StackOverflowClient.RegularStackOverflowClient;
import edu.java.clients.StackOverflowClient.StackOverflowClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfiguration {
    @Bean
    GitHubClient gitHubClient() {
        return new RegularGitHubClient();
    }

    @Bean
    StackOverflowClient stackOverflowClient() {
        return new RegularStackOverflowClient();
    }
}
