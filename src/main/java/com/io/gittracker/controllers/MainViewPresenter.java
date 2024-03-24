package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.AppState;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.TokenService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;
    private AppState appState = new AppState();

    @FXML
    private ListView<String> classes;

    @FXML
    private ListView<String> groups;

    @FXML
    private ListView<String> other;

    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList(
                appState.getWorkspaces().stream().map(Workspace::getName).toList());
        // Add sample items to the lists
        Workspace io = new Workspace("Inżynieria Oprogramowania");
        Workspace to = new Workspace("Technologie obiektowe");
        appState.addWorkspace(io);
        appState.addWorkspace(to);
        classes.setItems(items);
        groups.getItems().addAll("Grupa 1", "Grupa 2", "Grupa 4", "Grupa 4");
        other.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");
    }

    public MainViewPresenter(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }
}
