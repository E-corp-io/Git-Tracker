package com.io.gittracker;

import com.io.gittracker.model.PermaStorage;
import com.io.gittracker.services.AppStateService;
import java.io.IOException;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class AppConfiguration {

    AppStateService appStateService;

    public AppConfiguration(@Lazy AppStateService appStateService) {
        this.appStateService = appStateService;
    }

    @Bean
    public GitHub getGithub() throws IOException {
        String token = appStateService.getAppState().githubToken;
        return new GitHubBuilder().withOAuthToken(token).build();
    }

    @Bean
    public PermaStorage getPermaStorage() {
        return new PermaStorage();
    }
}
