package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.services.TokenService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;

@Component
public class TokenInputPresenter implements Initializable {
    private final TokenService tokenService;
    private final UIMain uiMain;

    @FXML
    public TextField ghToken;

    public TokenInputPresenter(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.ghToken.setText(tokenService.getApiKey());
    }

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
