package com.io.gittracker;

import com.io.gittracker.model.PermaStorage;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    private final TokenService tokenService;

    public AppConfiguration(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    public GitHub getGithub() throws IOException {
        return new GitHubBuilder().withOAuthToken(tokenService.getApiKey()).build();
    }

    @Bean
    public PermaStorage getPermaStorage() {
        return new PermaStorage();
    }
}
