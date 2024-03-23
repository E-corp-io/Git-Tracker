package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.services.TokenService;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class TokenInputPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;

    public TokenInputPresenter(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }

    @FXML
    public TextField ghToken;

    @FXML
    public void submitApiKey() {
        String token = ghToken.getText().trim();
        if (!token.isEmpty()) {
            this.tokenService.setApiKey(token);
            this.uiMain.loadSubjectView();
            System.out.println(token);
        }
    }
}
