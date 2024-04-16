package com.io.gittracker.controllers;

import static com.io.gittracker.model.Workspace.DEFAULT_GROUP;

import com.io.gittracker.UIMain;
import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.PullRequest;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import com.io.gittracker.services.TokenService;
import com.io.gittracker.utils.sorting.*;
import com.io.gittracker.view.RepositoryView;
import com.io.gittracker.view.WorkspaceView;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
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
    private CheckBox showClosedPrCheckbox;

    @FXML
    private Label clearAllFiltersLabel;

    @FXML
    private TextField searchTextField;

    @FXML
    private ToggleButton nameSortButton;

    @FXML
    private ToggleButton dateSortButton;

    @FXML
    private ToggleButton openPRCountButton;

    @FXML
    private ToggleButton ascendingButton;

    @FXML
    private ToggleButton descendingButton;

    @FXML
    private ProgressIndicator refreshProgress;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private ToggleGroup sortingStrategyToggleGroup;

    @FXML
    private ToggleGroup orderingToggleGroup;

    @Autowired
    private GithubService githubService;

    @FXML
    private ListView<Workspace> workspaceListView;

    @FXML
    private ListView<String> groupsListView;

    @FXML
    private ListView<String> otherListView;

    // WorkspaceViews are kept in map to easily switch between them
    // Each RepositoryPane is a repositoryPane.fxml
    private final Map<String, WorkspaceView> workspaceViewsMap = new HashMap<>();

    private WorkspaceView currentWorkspaceView;

    private final FilteringAndSortingPipeline pipeline = new FilteringAndSortingPipeline();

    public MainViewPresenter(TokenService tokenService, UIMain uiMain, AppStateService appStateService) {
        this.tokenService = tokenService;
        this.uiMain = uiMain;
        this.appStateService = appStateService;
    }

    public void initialize() {
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
                    setGraphic(null);
                } else {
                    setText(workspace.getName());

                    // TODO this diplays number of repos in workspace, currently not useful
                    //                    Label repoCount = new Label();
                    //                    repoCount
                    //                            .textProperty()
                    //                            .bind(workspace
                    //                                    .getRepositoriesProperty()
                    //                                    .map(List::size)
                    //                                    .map(String::valueOf));
                    //                    repoCount.setStyle("-fx-text-fill: #831a1a; -fx-font-weight: bold;");
                    //                    setGraphic(repoCount);
                }
            }
        });

        workspaceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) return;

            viewWorkspace(newValue);
            groupsListView.itemsProperty().bind(newValue.getGroupsProperty());
            if (this.currentWorkspaceView.getVisibleGroup() == null) {
                groupsListView.getSelectionModel().selectFirst();
            } else {
                groupsListView.getSelectionModel().select(this.currentWorkspaceView.getVisibleGroup());
            }
        });

        groupsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) newValue = DEFAULT_GROUP;
            pipeline.setGroupFilteringStrategy(new GroupFilteringStrategy(newValue));
            this.currentWorkspaceView.setVisibleGroup(newValue);
        });

        appStateService.getWorkspacesProperty().addListener(createWorkspaceChangeListener());

        workspaceListView.getItems().setAll(appStateService.getWorkspacesProperty());
        workspaceListView.getSelectionModel().selectFirst();

        sortingStrategyToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue == newValue) {
                return;
            }
            if (newValue == nameSortButton) {
                pipeline.setSortingStrategy(new NameSortingStrategy());
            } else if (newValue == dateSortButton) {
                pipeline.setSortingStrategy(new PrDateSortingStrategy());
            } else if (newValue == openPRCountButton) {
                pipeline.setSortingStrategy(new PrCountSortingStrategy());
            }
        });

        orderingToggleGroup.selectedToggleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue == newValue) {
                return;
            }

            if (newValue == ascendingButton) {
                pipeline.setReverseSortingStrategy(false);
            } else {
                pipeline.setReverseSortingStrategy(true);
            }
        });

        searchTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchByQuery();
            }
        });

        clearAllFiltersLabel.setOnMouseClicked(mouseEvent -> {
            pipeline.clearAllFilters();
            sortingStrategyToggleGroup.selectToggle(null);
            orderingToggleGroup.selectToggle(null);
            searchTextField.setText(null);
        });

        showClosedPrCheckbox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null && oldValue != newValue) {
                showClosedPrs(newValue);
            }
        });
        showClosedPrs(showClosedPrCheckbox.isSelected());
    }

    public void showClosedPrs(boolean showClosed) {
        Predicate<PullRequest> predicate = showClosed ? pr -> true : pr -> !pr.isClosed();
        workspaceViewsMap.values().forEach(workspaceView -> {
            workspaceView.filterPrRequests(predicate);
        });
    }

    void createDefaultContent() {
        Workspace io = new Workspace("Inżynieria Oprogramowania");
        Workspace to = new Workspace("Technologie obiektowe");
        Workspace os = new Workspace("Open Source");
        if (githubService.getGitHub() == null) {
            System.err.println("Github should not be null here!");
            return;
        }

        //        otherListView.getItems().addAll("Graded", "Not Graded", "Overdue", "Not Overdue");

        io.createAndAddNewGroup("G1");
        io.createAndAddNewGroup("G2");
        io.createAndAddNewGroup("G3");
        io.createAndAddNewGroup("G4");

        io.addRepositoryToGroup("G1", githubService.getRepository("E-corp-io/Git-Tracker"));
        to.addRepositoryToGroup("G1", githubService.getRepository("E-corp-io/GIT-TRACKER-API-TESTS"));
        os.addRepositoryToGroup("G1", githubService.getRepository("id-Software/DOOM"));

        appStateService.addWorkspace(io);
        appStateService.addWorkspace(to);
        appStateService.addWorkspace(os);
    }

    private Workspace currentWorkspace;

    private void viewWorkspace(Workspace workspace) {
        this.currentWorkspace = workspace;

        if (this.currentWorkspace == null) return;

        this.currentWorkspaceView = workspaceViewsMap.computeIfAbsent(workspace.getName(), name -> {
            WorkspaceView newWorkspaceView = new WorkspaceView(
                    createRepositoriesViewArray(this.currentWorkspace), pipeline.getSortingAndFilteringProperty());

            this.currentWorkspace
                    .getRepositoriesProperty()
                    .addListener(createRepositoryListChangeListener(newWorkspaceView));

            return newWorkspaceView;
        });
        scrollPane.setContent(this.currentWorkspaceView);
    }

    private RepositoryView[] createRepositoriesViewArray(Workspace workspace) {
        return workspace.getAllRepositories().stream()
                .map(this::createRepositoryView)
                .toArray(RepositoryView[]::new);
    }

    /** Listen to changes and create a view for new repositories  */
    private ListChangeListener<GithubRepository> createRepositoryListChangeListener(WorkspaceView workspaceView) {
        return (ListChangeListener.Change<? extends GithubRepository> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    List<? extends GithubRepository> addedRepositories = change.getAddedSubList();
                    RepositoryView[] repos = addedRepositories.stream()
                            .map(this::createRepositoryView)
                            .toArray(RepositoryView[]::new);
                    workspaceView.addRepositories(repos);
                }
            }
        };
    }

    private ListChangeListener<Workspace> createWorkspaceChangeListener() {
        return (ListChangeListener.Change<? extends Workspace> change) -> {
            while (change.next()) {
                if (change.wasAdded()) {

                    List<? extends Workspace> addedWorkspaces = change.getAddedSubList();
                    workspaceListView.getItems().addAll(addedWorkspaces);
                    workspaceListView.getSelectionModel().select(addedWorkspaces.get(0));
                }
            }
        };
    }

    private RepositoryView createRepositoryView(GithubRepository repository) {
        FXMLLoader fxmlLoader = new FXMLLoader(UIMain.class.getClassLoader().getResource("fxml/repositoryPane.fxml"));
        fxmlLoader.setControllerFactory(uiMain.getApplicationContext()::getBean);
        RepositoryView repositoryView = null;
        try {
            repositoryView = fxmlLoader.load();
            repositoryView.setRepository(repository);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (fxmlLoader.getController() instanceof RepositoryPresenter repositoryPresenter) {
            repositoryPresenter.initialize(repository, scrollPane);
        } else {
            System.err.println("Repository should have controller of class RepositoryPresenter");
        }
        return repositoryView;
    }

    @FXML
    public void handleAddNewRepoClicked(ActionEvent event) throws IOException {
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

    public void searchByQuery() {
        String query = searchTextField.getText();
        pipeline.setFilteringStrategy(new RepoNameFilteringStrategy(query));
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
