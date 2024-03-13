package com.io.gittracker.services;

import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private String apiKey;
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public String getApiKey() {
        return apiKey;
    }
}
