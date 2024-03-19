package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.services.TokenService;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;
    @FXML
    private ListView<String> classes;

    @FXML
    private ListView<String> groups;

    @FXML
    private ListView<String> other;

    public void initialize() {
        // Add sample items to the lists
        classes.getItems().addAll("Inżynieria Oprogramowania", "Technologie Obiektowe", "Projektowanie Obiektowe");
        groups.getItems().addAll("Grupa 1", "Grupa 2", "Grupa 4", "Grupa 4");
        other.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");
    }

    public MainViewPresenter(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }
}
