package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.Group;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import com.io.gittracker.services.TokenService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainViewPresenter {
    private final TokenService tokenService;
    private final UIMain uiMain;

    @Autowired
    private final AppStateService appStateService;

    @FXML
    public ProgressIndicator refreshProgress;

    public ScrollPane scrollPane;

    @Autowired
    private GithubService githubService;

    @FXML
    private ListView<Workspace> workspaceListView;

    @FXML
    private ListView<Group> groupsListView;

    @FXML
    private ListView<String> otherListView;

    @FXML
    private Label newRepoLabel;

    private final ListProperty<Workspace> workspacesProperty = new SimpleListProperty<>();

    // Overall structure,
    // WorkspaceView (VBox)
    //     GroupView (Vbox)
    //         RepositoryPane (TitledPane)
    //         RepositoryPane (TitledPane)
    //     GroupView (Vbox)
    //         RepositoryPane (TitledPane)
    //         RepositoryPane (TitledPane)

    // WorkspaceViews are kept in map to easily switch between them
    // Each RepositoryPane is a repositoryPane.fxml
    private final Map<String, VBox> workspaceViews = new HashMap<>();

    @FXML
    private VBox groupVBox;

    public MainViewPresenter(TokenService tokenService, UIMain uiMain, AppStateService appStateService) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
        this.appStateService = appStateService;
        System.out.println("Creating new MainViewPresenter");
    }

    @FXML
    public void initialize() {
        System.out.println("In Main View initialize()");
        if (appStateService.getAppState().getWorkspacesProperty().isEmpty()) {
            createDefaultContent();
        }

        // display workspace names in listView
        workspaceListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Workspace workspace, boolean empty) {
                super.updateItem(workspace, empty);
                if (empty || workspace == null) {
                    setText(null);
                } else {
                    setText(workspace.getName());
                }
            }
        });
        workspaceListView.setCellFactory(workspaceListView.getCellFactory());

        // display workspace names in listView
        groupsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Group group, boolean empty) {
                super.updateItem(group, empty);
                if (empty || group == null) {
                    setText(null);
                } else {
                    setText(group.getName());
                }
            }
        });

        workspaceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            viewWorkspace(newValue);
            List<Group> groups = newValue.getGroups();
            groupsListView.getItems().setAll(groups);
        });

        workspacesProperty.addListener((observableValue, oldValue, newValue) -> {
            workspaceListView.getItems().setAll(newValue);
        });
        workspacesProperty.unbind();
        workspacesProperty.bind(appStateService.getWorkspacesProperty());

        if (!workspacesProperty.isEmpty()) viewWorkspace(workspacesProperty.get(0));
    }

    void createDefaultContent() {
        Workspace io = new Workspace("Inżynieria Oprogramowania");
        Workspace to = new Workspace("Technologie obiektowe");
        Workspace os = new Workspace("Open Source");
        if (githubService.getGitHub() == null) {
            System.err.println("Github should not be null here!");
            return;
        }

        otherListView.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");

        io.createAndAddNewGroup("G1");
        io.createAndAddNewGroup("G2");
        io.createAndAddNewGroup("G3");
        io.createAndAddNewGroup("G4");

        io.addRepositoryToGroup("G1", githubService.getRepository("E-corp-io/Git-Tracker"));
        to.addRepositoryToDefaultGroup(githubService.getRepository("E-corp-io/GIT-TRACKER-API-TESTS"));
        os.addRepositoryToDefaultGroup(githubService.getRepository("id-Software/DOOM"));

        appStateService.addWorkspace(io);
        appStateService.addWorkspace(to);
        appStateService.addWorkspace(os);
    }

    private Workspace currentWorkspace;

    private void viewWorkspace(Workspace workspace) {
        this.currentWorkspace = workspace;
        if (this.currentWorkspace == null) return;

        VBox repositories = workspaceViews.computeIfAbsent(workspace.getName(), name -> {
            VBox vBox = new VBox(createWorkspaceView(this.currentWorkspace));
            this.currentWorkspace.getGroupsProperty().addListener(createGroupListChangeListener(vBox));
            return vBox;
        });
        scrollPane.setContent(repositories);
    }

    private Accordion[] createWorkspaceView(Workspace workspace) {
        return workspace.getGroups().stream().map(this::createGroupView).toArray(Accordion[]::new);
    }

    private Accordion createGroupView(Group group) {
        TitledPane[] panes =
                group.getRepositories().stream().map(this::createRepositoryPane).toArray(TitledPane[]::new);
        //        VBox vBox = new VBox(panes);
        Accordion accordion = new Accordion(panes);
        accordion.getStyleClass().add("group-view-vbox");
        accordion.prefHeight(0);
        group.getRepositoriesProperty().addListener(createGithubRepositoryListChangeListener(accordion));
        return accordion;
    }

    /** Listen to changes and create a view for new repositories  */
    private ListChangeListener<GithubRepository> createGithubRepositoryListChangeListener(Accordion accordion) {
        return (ListChangeListener.Change<? extends GithubRepository> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    List<? extends GithubRepository> addedRepositories = change.getAddedSubList();
                    addedRepositories.forEach(repo -> accordion.getPanes().add(createRepositoryPane(repo)));
                }
            }
        };
    }

    /** Listen to changes and create a view for new groups  */
    private ListChangeListener<Group> createGroupListChangeListener(VBox vBox) {
        return (ListChangeListener.Change<? extends Group> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    List<? extends Group> addedGroups = change.getAddedSubList();
                    addedGroups.forEach(repo -> vBox.getChildren().add(createGroupView(repo)));
                }
            }
        };
    }

    private TitledPane createRepositoryPane(GithubRepository repository) {
        FXMLLoader fxmlLoader = new FXMLLoader(UIMain.class.getClassLoader().getResource("fxml/repositoryPane.fxml"));
        fxmlLoader.setControllerFactory(uiMain.getApplicationContext()::getBean);
        TitledPane pane = null;
        try {
            pane = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (fxmlLoader.getController() instanceof RepositoryPresenter repositoryPresenter) {
            repositoryPresenter.initialize(repository, scrollPane);
        } else {
            System.err.println("Repository should have controller of class RepositoryPresenter");
        }
        return pane;
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

    public void refresh(MouseEvent _event) {
        refreshProgress.setVisible(true);
        ExecutorService refreshExecutor;
        refreshExecutor = Executors.newFixedThreadPool(10); // Use at max 10 threads to refresh repos

        var task = new RefreshTask(appStateService, refreshExecutor, githubService);
        task.setOnSucceeded(event -> {
            refreshProgress.setVisible(false);
        });
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    @FXML
    public void goToSettings(MouseEvent _mouseEvent) {
        this.uiMain.loadSettingsView();
    }

    static class RefreshTask extends Task<Void> {
        AppStateService appStateService;
        ExecutorService refreshExecutor;
        GithubService githubService;

        public RefreshTask(
                AppStateService appStateService, ExecutorService refreshExecutor, GithubService githubService) {
            this.appStateService = appStateService;
            this.refreshExecutor = refreshExecutor;
            this.githubService = githubService;
        }

        @Override
        protected Void call() {
            appStateService.refresh(githubService, refreshExecutor);
            //            var ending_task = new BullshitTask();
            refreshExecutor.shutdown();
            boolean is_terminated = false;
            while (!is_terminated) {
                try {
                    is_terminated = refreshExecutor.awaitTermination(100, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    //                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }
}
