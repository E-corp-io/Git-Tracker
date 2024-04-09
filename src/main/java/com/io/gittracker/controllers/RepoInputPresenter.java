package com.io.gittracker.controllers;

import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        if (workspace == null) return;
        String group = inputGroup.getText();
        System.out.println(
                "Addr: " + address + "; workspace: " + workspace.getName() + "; group: " + group + "; due on: ");
        // todo move this elsewhere
        workspace.addRepositoryToDefaultGroup(githubService.getRepository(address));

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
}
