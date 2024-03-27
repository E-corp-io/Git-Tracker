package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;
    private final AppStateService appStateService;
    private HostServices hostServices;

    @Autowired
    private GithubService githubService;

    @Autowired
    private void setHostServices(HostServices hostServices) {
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

    private final ObservableList<String> workspaces = FXCollections.observableArrayList();

    public void initialize() {
        Workspace io = new Workspace("Inżynieria Oprogramowania");
        Workspace to = new Workspace("Technologie obiektowe");
        Workspace os = new Workspace("Open Source");
        try {
            githubService.setGitHub();
        } catch (IOException e) {
            e.printStackTrace();
        }

        io.addRepositoryToDefaultGroup(githubService.getRepository("E-corp-io/Git-Tracker"));
        io.addRepositoryToDefaultGroup(githubService.getRepository("E-corp-io/GIT-TRACKER-API-TESTS"));
        os.addRepositoryToDefaultGroup(githubService.getRepository("nodejs/node"));
        appStateService.getAppState().addWorkspace(io);
        appStateService.getAppState().addWorkspace(to);
        appStateService.getAppState().addWorkspace(os);

        classes.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Selected value changed: " + newValue);
                setCurrentWorkspace(newValue);
                createTilesFromList();
            }
        });

        // Add sample items to the lists
        setWorkspaceList();
        groups.getItems().addAll("Grupa 1", "Grupa 2", "Grupa 4", "Grupa 4");
        other.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");
        createTilesFromList();
    }

    private Workspace currentWorkspace;

    private void setCurrentWorkspace(String name) {
        this.currentWorkspace = this.appStateService.getAppState().getWorkspaceByName(name);
    }

    private void setWorkspaceList() {
        this.workspaces.addAll(
                appStateService.getWorkspaces().stream().map(Workspace::getName).toList());
        classes.setItems(workspaces);
    }

    public void setList() {
        clearTileList();
        ObservableList<String> items = FXCollections.observableArrayList(
                appStateService.getWorkspaces().stream().map(Workspace::getName).toList());
        classes.setItems(items);
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
        if (this.currentWorkspace == null) {
            return;
        }
        this.currentWorkspace.getGroups().stream()
                .flatMap(group -> group.getRepositories().stream())
                .forEach(repo -> {
                    if (repo != null) {
                        repoBox.getChildren().add(createTile(repo));
                    }
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
        repoName.getStyleClass().add("clickable");
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/addRepo.fxml"));
        fxmlLoader.setControllerFactory(uiMain.getApplicationContext()::getBean);
        Parent root = fxmlLoader.load();
        Scene popupScene = new Scene(root);
        popupStage.setScene(popupScene);
        popupStage.setTitle("Add new repo");
        popupStage.show();
    }
}
