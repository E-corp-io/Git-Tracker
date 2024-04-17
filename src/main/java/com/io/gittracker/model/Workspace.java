package com.io.gittracker.model;

import com.io.gittracker.services.GithubService;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public final class Workspace implements Serializable, Refreshable {
    private final String name;

    public static final String DEFAULT_GROUP = "ALL";
    private ListProperty<String> groupsProperty = new SimpleListProperty<>(FXCollections.observableArrayList());

    private ListProperty<GithubRepository> repositoriesProperty =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    public Workspace(String name) {
        this.name = name;
        groupsProperty.add(DEFAULT_GROUP);
    }

    public String getName() {
        return name;
    }

    public boolean checkIfWorkspaceExists(String name) {
        for (String groupNames : groupsProperty) {
            if (groupNames.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<GithubRepository> getAllRepositories() {
        return repositoriesProperty.get();
    }

    public List<String> getGroups() {
        return new ArrayList<>(groupsProperty.getValue());
    }

    public ListProperty<String> getGroupsProperty() {
        return groupsProperty;
    }

    public void createAndAddNewGroup(String groupName) {
        groupsProperty.add(groupName);
    }

    public void addRepositoryToGroup(String groupName, GithubRepository repository) {
        repository.setGroupName(groupName);
        int idx = groupsProperty.indexOf(groupName);
        if (idx == -1) createAndAddNewGroup(groupName);
        repositoriesProperty.add(repository);
    }

    public ListProperty<GithubRepository> getRepositoriesProperty() {
        return repositoriesProperty;
    }

    public ListProperty<GithubRepository> repositoriesPropertyProperty() {
        return repositoriesProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workspace workspace = (Workspace) o;
        return Objects.equals(name, workspace.name) && Objects.equals(groupsProperty, workspace.groupsProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, groupsProperty);
    }

    @Override
    public String toString() {
        return """
                Workspace: %s
                Groups: %s
                """
                .formatted(name, String.join(", ", groupsProperty));
    }

    @Override
    public void refresh(GithubService githubService, ExecutorService executorService) {
        System.out.println("Refreshing workspace");
        repositoriesProperty.forEach(r -> r.refresh(githubService, executorService));
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(new ArrayList<>(groupsProperty.getValue()));
        out.writeObject(new ArrayList<>(repositoriesProperty.get()));
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        groupsProperty = new SimpleListProperty<>(FXCollections.observableList((List<String>) in.readObject()));
        repositoriesProperty =
                new SimpleListProperty<>(FXCollections.observableList((List<GithubRepository>) in.readObject()));
    }

    public void removeRepo(GithubRepository repo) {
        this.repositoriesProperty.remove(repo);
    }
}
