package com.io.gittracker.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private String apiKey;

    /** loads the key from application.properties if present, null otherwise */
    @Value("${api.key:}")
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }
}
