package com.io.gittracker.controllers;

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
    public DatePicker dateInput;

    @FXML
    public Label add_new_repo_msg;

    @FXML
    private ComboBox<Workspace> inputWorkspace;

    @FXML
    private TextField inputGroup;

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
        inputWorkspace.onActionProperty().set(event -> {
            confirmButton.setDisable(ShouldConfirmBeDisabled());
        });

        inputWorkspace.getItems().addAll(appStateService.getWorkspaces());
        inputWorkspace.setCellFactory(new Callback<ListView<Workspace>, ListCell<Workspace>>() {
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
        inputWorkspace.setConverter(new StringConverter<Workspace>() {
            @Override
            public String toString(Workspace object) {
                return object != null ? object.getName() : "";
            }

            @Override
            public Workspace fromString(String string) {
                if (string == null || string.trim().isEmpty()) {
                    return null;
                } else {
                    return new Workspace(string);
                }
            }
        });
    }

    @FXML
    private void handleConfirm(MouseEvent mouseEvent) throws IOException {
        String address = inputRepo.getText();
        Workspace workspace = inputWorkspace.getValue();
        if (workspace == null) return; // Should not happen
        String group = inputGroup.getText();
        System.out.println(
                "Addr: " + address + "; workspace: " + workspace.getName() + "; group: " + group + "; due on: ");
        // todo move this elsewhere

        if (repo_owner.isEmpty() || repo_name.isEmpty()) {
            System.err.println("Error: repo_owner or repo_name is empty");
            return;
        }

        var owner_slash_repo_name = String.format("%s/%s", repo_owner.get(), repo_name.get());
        workspace.addRepositoryToDefaultGroup(githubService.getRepository(owner_slash_repo_name));

        // TODO: make adding async - the repo url may be bad!

        boolean added = false;
        for (Workspace workspace1 : appStateService.getWorkspaces()) {
            if (workspace1.getName().equals(workspace.getName())) {
                added = true;
                workspace.getAllRepositories().forEach(workspace1::addRepositoryToDefaultGroup);
            }
        }
        if (!added) {
            appStateService.getAppState().addWorkspace(workspace);
        }
        for (Workspace w : this.appStateService.getWorkspaces()) {
            System.out.println(w.getName());
        }
        this.mainViewPresenter.setList();
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleCancel(MouseEvent mouseEvent) {
        ((Stage) this.cancelButton.getScene().getWindow()).close();
    }

    private boolean ShouldConfirmBeDisabled() {
        Workspace workspace = inputWorkspace.getValue();
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
