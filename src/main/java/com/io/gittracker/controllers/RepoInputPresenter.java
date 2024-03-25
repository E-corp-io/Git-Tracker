package com.io.gittracker.controllers;

import com.io.gittracker.services.AppStateService;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
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
    private TextField inputRepo;

    private AppStateService appStateService;

    @Autowired
    public void setAppStateService(AppStateService appStateService) {
        this.appStateService = appStateService;
    }

    @FXML
    private void handleConfirm(MouseEvent mouseEvent) {
        String address = inputRepo.getText();
        String workspace = inputWorkspace.getText();
        String group = inputGroup.getText();
        LocalDate dueDate = dateInput.getValue();

        System.out.println("Addr: " + address + "; workspace: " + workspace + "; group: " + group + "; due on: "
                + dueDate.toString());
        // actually add to appstate somehow
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(MouseEvent mouseEvent) {
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }
}
