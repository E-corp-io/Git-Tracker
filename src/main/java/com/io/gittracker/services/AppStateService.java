package com.io.gittracker.services;

import com.io.gittracker.model.AppState;
import com.io.gittracker.model.PermaStorage;
import com.io.gittracker.model.Workspace;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javafx.concurrent.Task;
import org.springframework.stereotype.Service;

@Service
public class AppStateService {

    private final PermaStorage permaStorage;
    private final AppState appState;

    public AppStateService(PermaStorage permaStorage) {
        this.permaStorage = permaStorage;
        appState = permaStorage.readState();
    }

    public AppState getAppState() {
        return appState;
    }

    public List<Workspace> getWorkspaces() {
        return appState.getWorkspaces();
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
        System.out.println("Refreshing app state");
        appState.getWorkspaces().forEach(w -> w.refresh(githubService, executorService));
    }


}
