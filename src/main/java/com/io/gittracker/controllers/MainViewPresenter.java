package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.AppState;
import com.io.gittracker.model.Repository;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    @FXML
    private Label newRepoLabel;

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
        initTestRepoList();
        createTilesFromList();
    }

    public MainViewPresenter(TokenService tokenService, UIMain uiMain) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
    }

    @FXML
    private TilePane repoBox;

    private ArrayList<Repository> repoList =
            new ArrayList<>(); // change to observable list once it actually changes + connect to the model

    public void initTestRepoList() { // just for testing purposes
        Repository repo1 = new Repository(
                "Git-tracker",
                "https://github.com/E-corp-io/Git-Tracker",
                4.0f,
                "",
                LocalDate.of(2024, 4, 25),
                new ArrayList<String>(List.of("graded")),
                "Inżynieria oprogramowania",
                "Grupa 6");
        repoList.add(repo1);
    }

    public void createTilesFromList() {
        clearTileList();
        repoList.forEach(repo -> {
            repoBox.getChildren().add(createTile(repo));
        });
    }

    private void clearTileList() {
        this.repoBox.getChildren().clear();
    }

    private VBox createTile(Repository repo) {
        VBox tile = new VBox();
        HBox upperRow = new HBox();
        HBox lowerRow = new HBox();
        Label repoName = new Label(repo.getGithubName());
        Label lastCommit = new Label("last commit msg should go here");
        upperRow.getChildren().add(repoName);
        lowerRow.getChildren().add(lastCommit);
        tile.getChildren().add(upperRow);
        tile.getChildren().add(lowerRow);
        return tile;
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
        popupStage.setScene(popupScene);
        popupStage.setTitle("Add new repo");
        popupStage.show();
    }
}
