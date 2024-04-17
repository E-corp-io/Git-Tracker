package com.io.gittracker.services;

import com.io.gittracker.model.AppState;
import com.io.gittracker.model.PermaStorage;
import com.io.gittracker.model.Workspace;
import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import javafx.beans.property.ListProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AppStateService {

    private final PermaStorage permaStorage;
    private final AppState appState;

    private final Logger logger = LoggerFactory.getLogger(AppStateService.class);

    public AppStateService(PermaStorage permaStorage) {
        this.permaStorage = permaStorage;
        appState = permaStorage.readState();
    }

    public AppState getAppState() {
        return appState;
    }

    public List<Workspace> getWorkspaces() {
        return appState.getWorkspacesProperty();
    }

    public ListProperty<Workspace> getWorkspacesProperty() {
        return appState.getWorkspacesProperty();
    }

    public void saveAppState() {
        permaStorage.saveState(appState);
    }

    public void saveAppState(AppState appState) {
        permaStorage.saveState(appState);
    }

    /**
     * fetches all data for API and propagates the changes
     */
    public void refresh(GithubService githubService, ExecutorService executorService) {
        logger.info("Refreshing app state");
        appState.getWorkspacesProperty().forEach(w -> w.refresh(githubService, executorService));
    }

    public void addWorkspace(Workspace workspace) {
        appState.getWorkspacesProperty().add(workspace);
    }

    public void loadStateFromFile(File file) {
        AppState newState = permaStorage.readState(file);
        this.appState
                .getWorkspacesProperty()
                .setValue(newState.getWorkspacesProperty().getValue());
    }

    public void saveStateToFile(File file) {
        permaStorage.saveState(appState, file);
    }
}
