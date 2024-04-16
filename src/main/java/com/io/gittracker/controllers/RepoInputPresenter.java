package com.io.gittracker.controllers;

import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.Group;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

@Component
public class RepoInputPresenter {

    @FXML
    public Label add_new_repo_msg;

    @FXML
    private ComboBox<Workspace> workspaceComboBox;

    @FXML
    private ComboBox<Group> groupComboBox;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @FXML
    private TextField inputRepo;

    private AppStateService appStateService;
    private GithubService githubService;

    Optional<String> platform = Optional.empty();
    Optional<String> repo_owner = Optional.empty();
    Optional<String> repo_name = Optional.empty();

    // todo consider removing MainViewPresenter from here
    private MainViewPresenter mainViewPresenter;

    public RepoInputPresenter(
            GithubService githubService, AppStateService appStateService, MainViewPresenter mainViewPresenter) {
        this.githubService = githubService;
        this.appStateService = appStateService;
        this.mainViewPresenter = mainViewPresenter;
    }

    @FXML
    void initialize() {
        confirmButton.setDisable(true);
        configureWorkspaceComboBox();
        configureGroupComboBox();
    }

    private void configureWorkspaceComboBox() {

        workspaceComboBox.onActionProperty().set(event -> {
            confirmButton.setDisable(ShouldConfirmBeDisabled());
        });

        workspaceComboBox.itemsProperty().bind(appStateService.getWorkspacesProperty());
        workspaceComboBox.setCellFactory(new Callback<ListView<Workspace>, ListCell<Workspace>>() {
            @Override
            public ListCell<Workspace> call(ListView<Workspace> param) {
                return new ListCell<Workspace>() {
                    @Override
                    protected void updateItem(Workspace item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        workspaceComboBox.setConverter(new StringConverter<Workspace>() {
            @Override
            public String toString(Workspace object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Workspace fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                } else {
                    for (Workspace workspace : appStateService.getWorkspacesProperty()) {
                        if (workspace.getName().equals(string)) {
                            return workspace;
                        }
                    }
                    return new Workspace(string);
                }
            }
        });
        workspaceComboBox.getSelectionModel().selectFirst();
    }

    private void configureGroupComboBox() {

        workspaceComboBox.valueProperty().addListener((observableValue, oldWorkspace, newWorkspace) -> {
            if (newWorkspace != null) {
                groupComboBox.getItems().clear();
                groupComboBox.getItems().addAll(newWorkspace.getGroups());
            }
            groupComboBox.getSelectionModel().selectFirst();
        });
        groupComboBox.setCellFactory(new Callback<ListView<Group>, ListCell<Group>>() {
            @Override
            public ListCell<Group> call(ListView<Group> param) {
                return new ListCell<Group>() {
                    @Override
                    protected void updateItem(Group group, boolean empty) {
                        super.updateItem(group, empty);
                        if (group != null) {
                            setText(group.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });
        groupComboBox.setConverter(new StringConverter<Group>() {
            @Override
            public String toString(Group group) {
                return group != null ? group.getName() : "";
            }

            @Override
            public Group fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                } else {
                    for (Group group : workspaceComboBox.getValue().getGroups()) {
                        if (group.getName().equals(string)) return group;
                    }
                    return new Group(string);
                }
            }
        });
        groupComboBox.getItems().setAll(workspaceComboBox.getValue().getGroups());
        groupComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleConfirm(MouseEvent mouseEvent) throws IOException {
        String address = inputRepo.getText();
        Workspace workspace = workspaceComboBox.getValue();
        if (workspace == null) return; // Should not happen
        Group group = groupComboBox.getValue();

        System.out.printf("Adding %s to group %s and workspace %s%n", address, group.getName(), workspace.getName());

        // todo move this elsewhere

        if (repo_owner.isEmpty() || repo_name.isEmpty()) {
            System.err.println("Error: repo_owner or repo_name is empty");
            return;
        }

        var owner_slash_repo_name = String.format("%s/%s", repo_owner.get(), repo_name.get());
        GithubRepository repository = githubService.getRepository(owner_slash_repo_name);

        if (repository == null) return;

        if (!appStateService.getWorkspacesProperty().contains(workspace)) {
            appStateService.getWorkspacesProperty().add(workspace);
        }
        if (!workspace.getGroups().contains(group)) {
            group.setWorkspace(workspace);
            workspace.getGroupsProperty().add(group);
        }

        group.addRepository(repository);

        // TODO: make adding async - the repo url may be bad!
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(MouseEvent mouseEvent) {
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    private boolean ShouldConfirmBeDisabled() {
        Workspace workspace = workspaceComboBox.getValue();
        boolean bad_workspace = workspace == null;
        boolean bad_platform = platform.isEmpty();
        boolean bad_repo_owner = repo_owner.isEmpty();
        boolean bad_repo_name = repo_name.isEmpty();
        return bad_workspace || bad_platform || bad_repo_owner || bad_repo_name;
    }

    public void parseUrl(String urlString) {
        System.out.printf("Got url: %s\n", urlString);
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            int port = url.getPort();
            if (port <= 0) {
                port = url.getDefaultPort();
            }
            String path = url.getPath();

            switch (host) {
                case "github.com":
                    platform = Optional.of("Github");
                    break;

                default:
                    platform = Optional.empty();
                    break;
            }

            String[] path_segments = path.split("/");
            if (path_segments.length < 3) {
                repo_owner = Optional.empty();
                repo_name = Optional.empty();
            } else {
                // GOOD case
                repo_owner = Optional.of(path_segments[1]);
                repo_name = Optional.of(path_segments[2]);
            }

            return;
        } catch (MalformedURLException e) {
            // Just ignore it
        }
        platform = Optional.empty();
        repo_owner = Optional.empty();
        repo_name = Optional.empty();
    }

    public void userTyped(KeyEvent _keyEvent) {
        parseUrl(inputRepo.getText().trim());
        var new_text = String.format(
                "Add new repository: %s/%s/%s",
                platform.orElseGet(() -> "?"), repo_owner.orElseGet(() -> "?"), repo_name.orElseGet(() -> "?"));
        this.add_new_repo_msg.setText(new_text);

        confirmButton.setDisable(ShouldConfirmBeDisabled());
    }
}
