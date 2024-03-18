package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.services.TokenService;
import org.springframework.stereotype.Component;

@Component
public class SubjectView {
    private final TokenService tokenService;
    private final UIMain uiMain;

    public SubjectView(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }
}
