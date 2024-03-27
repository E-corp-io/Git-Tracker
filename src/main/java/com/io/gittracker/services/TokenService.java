package com.io.gittracker.services;

import com.io.gittracker.model.AppState;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    AppState app_state;

    TokenService(AppStateService appStateService) {
        this.app_state = appStateService.getAppState();
    }

    public void setApiKey(String apiKey) {
        this.app_state.githubToken = apiKey;
    }

    public String getApiKey() {
        return this.app_state.githubToken;
    }
}
