package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.AppState;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;
    private AppState appState = new AppState(
            new ArrayList<>(List.of(
                    new Workspace("Inżynieria Oprogramowania", null), new Workspace("Technologie obiektowe", null))),
            0,
            0,
            0,
            new Date());

    @FXML
    private ListView<String> classes;

    @FXML
    private ListView<String> groups;

    @FXML
    private ListView<String> other;

    @FXML
    private Label newRepoLabel;

    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList(
                appState.getWorkspaces().stream().map(Workspace::getName).toList());
        // Add sample items to the lists
        classes.setItems(items);
        groups.getItems().addAll("Grupa 1", "Grupa 2", "Grupa 4", "Grupa 4");
        other.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");
    }

    public MainViewPresenter(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }

    @FXML
    public void handleAddNewRepoClicked(MouseEvent event) throws IOException {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        Node node = (Node) event.getSource();
        Stage thisStage = (Stage) node.getScene().getWindow();
        popupStage.initOwner(thisStage);
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/addRepo.fxml")));
        Scene popupScene = new Scene(root);
        popupScene
                .getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css"))
                        .toExternalForm());
        popupStage.setScene(popupScene);
        popupStage.setTitle("Add new repo");
        popupStage.show();
    }
}
