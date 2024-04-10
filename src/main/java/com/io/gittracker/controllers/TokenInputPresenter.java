package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.services.GithubService;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

@Component
public class TokenInputPresenter implements Initializable {
    private final TokenService tokenService;
    private final GithubService githubService;
    private final UIMain uiMain;

    @FXML
    public TextField ghToken;

    @FXML
    public Button submitButton;

    @FXML
    public ProgressIndicator progressIndicator;

    @FXML
    public Label error_string;

    public TokenInputPresenter(TokenService tokenService, GithubService githubService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.githubService = githubService;
        this.uiMain = uiMain;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.ghToken.setText(tokenService.getApiKey());
        progressIndicator.setProgress(-1.0);

        var token = ghToken.getText();
        CreateAndRunTask(token);
    }

    @FXML
    public void submitApiKey() {
        String token = ghToken.getText().trim();

        if (!token.isEmpty()) {
            this.tokenService.setApiKey(token);
            CreateAndRunTask(token);
        }
    }

    CreateGithubTask CreateTask(String token) {
        var task = new CreateGithubTask();
        task.setOnSucceeded(event -> {
            this.progressIndicator.setVisible(false);
            submitButton.setDisable(false);
            Optional<GitHub> optional = task.valueProperty().get();
            if (optional.isEmpty()) {
                error_string.setText("Github login failed");
                return;
            }
            var github = optional.get();

            if (github.isCredentialValid()) {
                githubService.SetGithub(github);
                this.uiMain.loadSubjectView();
            } else {
                error_string.setText("Github login failed");
            }
        });

        task.setOnScheduled((WorkerStateEvent event) -> submitButton.setDisable(true));
        task.SetToken(token);
        return task;
    }

    void CreateAndRunTask(String token) {
        var task = CreateTask(token);
        error_string.setText("");
        Thread th = new Thread(task);
        this.progressIndicator.setVisible(true);
        th.setDaemon(true);
        th.start();
    }

    static class CreateGithubTask extends Task<Optional<GitHub>> {

        String token = "";

        public void SetToken(String token) {
            this.token = token;
        }

        @Override
        protected Optional<GitHub> call() {
            try {
                return Optional.of(new GitHubBuilder().withOAuthToken(token).build());
            } catch (IOException e) {
                return Optional.empty();
            }
        }
    }
}
