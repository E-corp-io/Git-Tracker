package com.io.gittracker.controllers;

import com.io.gittracker.services.AppStateService;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RepoInputPresenter {

    @FXML
    public DatePicker dateInput;

    @FXML
    private TextField inputWorkspace;

    @FXML
    private TextField inputGroup;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField repo_url;

    @FXML
    private Label repo_owner;

    @FXML
    private Label repo_name;

    @FXML
    private Label platform;

    private AppStateService appStateService;

    @Autowired
    public void setAppStateService(AppStateService appStateService) {
        this.appStateService = appStateService;
    }

    @FXML
    private void handleConfirm(MouseEvent mouseEvent) {
        String owner_slash_repoName = repo_owner.getText() + '/' + repo_name.getText();
        String workspace = inputWorkspace.getText();
        String group = inputGroup.getText();
        LocalDate dueDate = dateInput.getValue();

        System.out.println("Addr: " + owner_slash_repoName + "; workspace: " + workspace + "; group: " + group
                + "; due on: " + dueDate.toString());
        // actually add to appstate somehow
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(MouseEvent mouseEvent) {
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void userTyped(StringProperty property, String oldValue, String newValue) {
        parseUrl(repo_url.getText());
    }

    public void parseUrl(String urlString) {
        // TODO: control submit btn arm state
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getPort();
            if (port <= 0) {
                port = url.getDefaultPort();
            }
            String path = url.getPath();

            switch (host) {
                case "github.com":
                    platform.setText("Github");
                    break;

                default:
                    platform.setText("Unknown platform");
                    break;
            }

            String[] path_segments = path.split("/");
            if (path_segments.length < 3) {
                repo_owner.setText("?OWNER?");
                repo_name.setText("?NAME?");
            } else {
                // GOOD case
                repo_owner.setText(path_segments[1]);
                repo_name.setText(path_segments[2]);
            }

            return;
        } catch (MalformedURLException e) {
            // Just ignore it
        }
        repo_owner.setText("ERROR");
        repo_name.setText("ERROR");
        platform.setText("ERROR");
    }
}
