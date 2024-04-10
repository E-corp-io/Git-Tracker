package com.io.gittracker.model;

import com.io.gittracker.services.GithubService;

import java.util.concurrent.ExecutorService;

/**
 * Interface for objects that can be refreshed via an API to check for updates.
 */
public interface Refreshable {
    /**
     * Refreshes the object's data from the server.
     */
    void refresh(GithubService githubService, ExecutorService executorService);
}
