package com.io.gittracker.utils;

import com.io.gittracker.model.AppState;

public class StateDisplay {
    public static void printAppState(AppState appState) {
        appState.getWorkspaces().forEach(workspace -> {
            System.out.println("================  " + workspace.getName() + "  =================");
            workspace.getAllRepositories().forEach(repo -> {
                System.out.println("Repository name: " + repo.getName());
                repo.getPullRequests().forEach(pullRequest -> {
                    System.out.println("    " + pullRequest);
                    pullRequest.getComments().forEach(prComment -> System.out.println("        " + prComment));
                    System.out.println();
                });
                System.out.println();
            });
            System.out.println();
        });
    }
}
