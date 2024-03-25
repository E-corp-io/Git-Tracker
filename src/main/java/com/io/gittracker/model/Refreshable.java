package com.io.gittracker.model;

/**
 * Interface for objects that can be refreshed via an API to check for updates.
 */
public interface Refreshable {
    /**
     * Refreshes the object's data from the server.
     */
    void refresh();
}
