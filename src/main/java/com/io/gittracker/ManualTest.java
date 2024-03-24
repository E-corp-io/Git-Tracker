package com.io.gittracker;

import com.io.gittracker.model.AppState;
import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.Workspace;
import com.io.gittracker.services.AppStateService;
import com.io.gittracker.services.GithubService;
import com.io.gittracker.utils.StateDisplay;
import org.springframework.stereotype.Component;

@Component
public class ManualTest {
    private final String token = "<PERSONAL_TOKEN>";

    private final GithubService githubService;

    private final AppStateService appStateService;

    public ManualTest(GithubService githubService, AppStateService appStateService) {
        this.githubService = githubService;
        this.appStateService = appStateService;
    }

    public void ManualTest() {
        AppState appState = new AppState();

        Workspace workspace = new Workspace("Inżynieria Oprogramowania");
        GithubRepository githubRepository = githubService.getRepository("E-corp-io/GIT-TRACKER-API-TESTS");
        GithubRepository githubRepository2 = githubService.getRepository("E-corp-io/Git-Tracker");

        workspace.addRepositoryToDefaultGroup(githubRepository);
        workspace.addRepositoryToDefaultGroup(githubRepository2);

        appState.addWorkspace(workspace);
        appState.addWorkspace(new Workspace("EMPTY WORKSPACE"));

        StateDisplay.printAppState(appState);

        appStateService.saveAppState(new AppState());

        appState.addWorkspace(new Workspace("TRASH"));
        workspace.createNewGroup(0, "TRASH GROUP");

        appState = appStateService.getAppState();

        StateDisplay.printAppState(appState);
    }
}
