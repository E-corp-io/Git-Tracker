package com.io.gittracker.controllers;

import com.io.gittracker.services.TokenService;
import javafx.fxml.FXML;
import org.springframework.stereotype.Component;
import javafx.scene.control.TextField;

@Component
public class TokenInput {
    private final TokenService tokenService;
    public TokenInput(TokenService tokenService){
        this.tokenService = tokenService;
    }
    @FXML
    public TextField ghToken;
    @FXML
    public void submitApiKey(){
        String token = ghToken.getText().trim();
        if (!token.isEmpty()) {
            this.tokenService.setApiKey(token);
            System.out.println(token);
        }
    }
}
