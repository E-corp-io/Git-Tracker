package com.io.gittracker.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class AppState implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private final List<Workspace> workspaces;
    private int currentWorkspaceIndex;
    private int currentGroupIndex;
    public String githubToken = "EMPTY";

    private final Date lastUpdate;

    /**
     * creates a default empty appState
     */
    public AppState() {
        this(new ArrayList<>(), 0, 0, new Date());
    }

    public AppState(List<Workspace> workspaces, int currentWorkspaceIndex, int currentGroupIndex, Date lastUpdate) {
        this.workspaces = workspaces;
        this.currentWorkspaceIndex = currentWorkspaceIndex;
        this.currentGroupIndex = currentGroupIndex;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AppState) obj;
        return Objects.equals(this.workspaces, that.workspaces)
                && this.currentWorkspaceIndex == that.currentWorkspaceIndex
                && this.currentGroupIndex == that.currentGroupIndex
                && Objects.equals(this.lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspaces, currentWorkspaceIndex, currentGroupIndex, lastUpdate);
    }

    @Override
    public String toString() {
        return "AppState[" + "workspaces="
                + workspaces + ", " + "currentWorkspaceIndex="
                + currentWorkspaceIndex + ", " + "currentGroupIndex="
                + currentGroupIndex + ", " + "currentSubgroupIndex="
                + lastUpdate + ']';
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }

    public void addWorkspace(Workspace workspace) {
        workspaces.add(workspace);
    }

    public boolean checkIfWorkspaceExists(String name) {
        for (Workspace workspace : workspaces) {
            if (workspace.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addNewRepo(String name, String url, String workspace, String group, LocalDate dueDate) {
        // check if workspace and group exist, if not - create them, create new repository, add where needed
    }

    public int getCurrentWorkspaceIndex() {
        return currentWorkspaceIndex;
    }

    public int getCurrentGroupIndex() {
        return currentGroupIndex;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setCurrentWorkspaceIndex(int index) {
        currentWorkspaceIndex = index;
    }

    public void setCurrentGroupIndex(int index) {
        currentGroupIndex = index;
    }

    public void setLastUpdate(Date date) {
        lastUpdate.setTime(date.getTime());
    }
}
