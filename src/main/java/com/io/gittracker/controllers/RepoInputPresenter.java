package com.io.gittracker.controllers;

import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import java.io.IOException;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class RepoInputPresenter {

    @FXML
    public DatePicker dateInput;

    @FXML
    private TextField inputWorkspace;

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
    private void handleConfirm(MouseEvent mouseEvent) throws IOException {
        String address = inputRepo.getText();
        String workspaceName = inputWorkspace.getText();
        String group = inputGroup.getText();
        LocalDate dueDate = dateInput.getValue();

        System.out.println("Addr: " + address + "; workspace: " + workspaceName + "; group: " + group + "; due on: "
                + dueDate.toString());
        // actually add to appstate somehow
        // todo move this elsewhere
        this.githubService.setGitHub();
        var appState = this.appStateService.getAppState();
        var workspace = appState.getOrCreate(workspaceName);
        workspace.addRepositoryToDefaultGroup(githubService.getRepository(address));
        System.out.println("Workspaces");
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
