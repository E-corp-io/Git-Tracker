package com.io.gittracker.model;

import java.io.*;
import java.util.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public final class AppState implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    private transient ListProperty<Workspace> workspacesProperty;
    private int currentWorkspaceIndex;
    private int currentGroupIndex;
    public String githubToken = "EMPTY";

    private final Date lastUpdate;

    /**
     * creates a default empty appState
     */
    public AppState() {
        this(FXCollections.observableArrayList(), 0, 0, new Date());
    }

    public static AppState createDefault() {
        return new AppState();
    }

    public AppState(List<Workspace> workspaces, int currentWorkspaceIndex, int currentGroupIndex, Date lastUpdate) {
        this.workspacesProperty = new SimpleListProperty<>(FXCollections.observableList(workspaces));
        this.currentWorkspaceIndex = currentWorkspaceIndex;
        this.currentGroupIndex = currentGroupIndex;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AppState) obj;
        return Objects.equals(this.workspacesProperty, that.workspacesProperty)
                && this.currentWorkspaceIndex == that.currentWorkspaceIndex
                && this.currentGroupIndex == that.currentGroupIndex
                && Objects.equals(this.lastUpdate, that.lastUpdate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workspacesProperty.getValue(), currentWorkspaceIndex, currentGroupIndex, lastUpdate);
    }

    @Override
    public String toString() {
        return "AppState[" + "workspaces="
                + workspacesProperty.getValue() + ", " + "currentWorkspaceIndex="
                + currentWorkspaceIndex + ", " + "currentGroupIndex="
                + currentGroupIndex + ", " + "currentSubgroupIndex="
                + lastUpdate + ']';
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(new ArrayList<>(workspacesProperty));
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        workspacesProperty = new SimpleListProperty<>(FXCollections.observableList((List<Workspace>) in.readObject()));
    }

    public ListProperty<Workspace> getWorkspacesProperty() {
        return workspacesProperty;
    }

    public void addWorkspace(Workspace workspace) {
        workspacesProperty.add(workspace);
    }

    public Workspace getOrCreate(String name) {
        for (Workspace workspace : workspacesProperty) {
            if (workspace.getName().equals(name)) {
                return workspace;
            }
        }
        var workspace = new Workspace(name);
        addWorkspace(workspace);
        return workspace;
    }

    public Workspace getWorkspaceByName(String name) {
        for (Workspace workspace : workspacesProperty) {
            if (workspace.getName().equals(name)) {
                return workspace;
            }
        }
        return null;
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
