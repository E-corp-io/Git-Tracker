package com.io.gittracker.model;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public final class AppState implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private final List<Workspace> workspaces;
    private int currentWorkspaceIndex;
    private int currentGroupIndex;
    private int currentSubgroupIndex;

    private final Date lastUpdate;

    public AppState(
            List<Workspace> workspaces,
            int currentWorkspaceIndex,
            int currentGroupIndex,
            int currentSubgroupIndex,
            Date lastUpdate) {
        this.workspaces = workspaces;
        this.currentWorkspaceIndex = currentWorkspaceIndex;
        this.currentGroupIndex = currentGroupIndex;
        this.currentSubgroupIndex = currentSubgroupIndex;
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
                && this.currentSubgroupIndex == that.currentSubgroupIndex
                && Objects.equals(this.lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspaces, currentWorkspaceIndex, currentGroupIndex, currentSubgroupIndex, lastUpdate);
    }

    @Override
    public String toString() {
        return "AppState[" + "workspaces="
                + workspaces + ", " + "currentWorkspaceIndex="
                + currentWorkspaceIndex + ", " + "currentGroupIndex="
                + currentGroupIndex + ", " + "currentSubgroupIndex="
                + currentSubgroupIndex + ", " + "lastUpdate="
                + lastUpdate + ']';
    }

    public List<Workspace> getWorkspaces() {
        return workspaces;
    }

    public int getCurrentWorkspaceIndex() {
        return currentWorkspaceIndex;
    }

    public int getCurrentGroupIndex() {
        return currentGroupIndex;
    }

    public int getCurrentSubgroupIndex() {
        return currentSubgroupIndex;
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

    public void setCurrentSubgroupIndex(int index) {
        currentSubgroupIndex = index;
    }

    public void setLastUpdate(Date date) {
        lastUpdate.setTime(date.getTime());
    }
}
