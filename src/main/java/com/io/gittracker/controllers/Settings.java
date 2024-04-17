package com.io.gittracker.controllers;

import com.io.gittracker.services.AppStateService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class Settings {
    public TextField downloadLimit;
    public Button exitButton;
    private AppStateService appStateService;

    Settings(AppStateService appStateService) {
        this.appStateService = appStateService;
    }

    @FXML
    void initialize() {
        // force the field to be numeric only
        downloadLimit.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d*")) {
                    downloadLimit.setText(newValue.replaceAll("[^\\d]", ""));
                }
                int new_value = Integer.parseInt(downloadLimit.getText());
                appStateService.getAppState().pr_download_limit = new_value;
            }
        });
        downloadLimit.setText(Integer.toString(appStateService.getAppState().pr_download_limit));
    }

    public void exitButtonClicked(MouseEvent event) {
        ((Stage) this.exitButton.getScene().getWindow()).close();
    }
}
