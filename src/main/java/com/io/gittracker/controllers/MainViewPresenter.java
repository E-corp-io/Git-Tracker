package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.AppState;
import com.io.gittracker.model.Repository;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.TokenService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;

    private HostServices hostServices;

    @Autowired
    private void getHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
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

    public void initialize() {
        ObservableList<String> items = FXCollections.observableArrayList(
                appState.getWorkspaces().stream().map(Workspace::getName).toList());
        // Add sample items to the lists
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

    private ArrayList<Repository> repoList = new ArrayList<>(); // change to observable list once it actually changes + connect to the model

    public void initTestRepoList() { // just for testing purposes
        Repository repo1 = new Repository("Git-tracker", "https://github.com/E-corp-io/Git-Tracker",
                4.0f, "", LocalDate.of(2024,4,25), new ArrayList<String>(List.of("graded")), "Inżynieria oprogramowania", "Grupa 6");
        repoList.add(repo1);
        Repository repo2 = new Repository("Git-tracker2", "https://github.com/E-corp-io/Git-Tracker",
                3.0f, "", LocalDate.of(2024,4,25), new ArrayList<String>(List.of("graded")), "Inżynieria oprogramowania", "Grupa 6");
        repoList.add(repo2);
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
        tile.getStyleClass().add("repoTile");
        HBox upperRow = new HBox();
        HBox lowerRow = new HBox();
        Label repoName = new Label(repo.getGithubName());
        repoName.setOnMouseClicked(event -> {
            System.out.println(repo.getUrl());
            this.hostServices.showDocument(repo.getUrl());
        });
        Label lastCommit = new Label("last commit msg should go here");
        upperRow.getChildren().add(repoName);
        lowerRow.getChildren().add(lastCommit);
        tile.getChildren().add(upperRow);
        tile.getChildren().add(lowerRow);
        return tile;
    }
}
