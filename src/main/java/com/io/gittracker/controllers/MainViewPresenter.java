package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.AppState;
import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.Repository;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
import java.util.ArrayList;
import javafx.application.HostServices;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;
//    private AppState appState = new AppState();
    private final AppStateService appStateService;
    private HostServices hostServices;

    @Autowired
    private void getHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

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
                appStateService.getWorkspaces().stream().map(Workspace::getName).toList());
        // Add sample items to the lists
        Workspace io = new Workspace("Inżynieria Oprogramowania");
        Workspace to = new Workspace("Technologie obiektowe");
        appStateService.getAppState().addWorkspace(io);
        appStateService.getAppState().addWorkspace(to);
        classes.setItems(items);
        groups.getItems().addAll("Grupa 1", "Grupa 2", "Grupa 4", "Grupa 4");
        other.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");
        createTilesFromList();
    }

    public MainViewPresenter(TokenService tokenService, UIMain uiMain, AppStateService appStateService) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
        this.appStateService = appStateService;
    }

    @FXML
    private TilePane repoBox;

    public void createTilesFromList() {
        clearTileList();
        appStateService.getWorkspaces().stream().flatMap(workspace -> workspace.getGroups().stream().flatMap(group -> group.getRepositories().stream())).forEach(repo -> {
            repoBox.getChildren().add(createTile(repo));
        });
    }

    private void clearTileList() {
        this.repoBox.getChildren().clear();
    }

    private VBox createTile(GithubRepository repo) {
        VBox tile = new VBox();
        tile.getStyleClass().add("repoTile");
        HBox upperRow = new HBox();
        HBox lowerRow = new HBox();
        Label repoName = new Label(repo.getName());
        repoName.setOnMouseClicked(event -> {
            System.out.println(repo.getHtmlUrl().toString());
            this.hostServices.showDocument(repo.getHtmlUrl().toString());
        });
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
        popupScene
                .getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/styles.css"))
                        .toExternalForm());
        popupStage.setScene(popupScene);
        popupStage.setTitle("Add new repo");
        popupStage.show();
    }
}
