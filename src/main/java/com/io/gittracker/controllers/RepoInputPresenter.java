package com.io.gittracker.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class RepoInputPresenter {

    @FXML
    private TextField inputWorkspace;
    @FXML
    private TextField inputGroup;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField inputRepo;


    @FXML
    private void handleConfirm(MouseEvent mouseEvent) {
        String address = inputRepo.getText();
        String workspace = inputWorkspace.getText();
        String group = inputGroup.getText();

        System.out.println("Addr: " + address + "; workspace: " + workspace + "; group: " + group);
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(MouseEvent mouseEvent) {
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }
}
