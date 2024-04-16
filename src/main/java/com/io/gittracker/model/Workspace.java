package com.io.gittracker.model;

import com.io.gittracker.services.GithubService;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public final class Workspace implements Serializable, Refreshable {
    private final String name;
    private transient ListProperty<Group> groupsProperty =
            new SimpleListProperty<>(FXCollections.observableArrayList());

    public Workspace(String name) {
        this.name = name;
        groupsProperty.add(getDefaultGroup());
    }

    public String getName() {
        return name;
    }

    public boolean checkIfWorkspaceExists(String name) {
        for (Group group : groupsProperty) {
            if (group.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<GithubRepository> getAllRepositories() {
        return groupsProperty.stream()
                .flatMap(group -> group.getRepositories().stream())
                .toList();
    }

    public List<Group> getGroups() {
        return new ArrayList<>(groupsProperty.getValue());
    }

    public ListProperty<Group> getGroupsProperty() {
        return groupsProperty;
    }

    public Group createAndAddNewGroup(String name) {
        Group group = new Group(name);
        groupsProperty.add(group);
        return group;
    }

    public void addRepositoryToDefaultGroup(GithubRepository repository) {
        if (repository == null) return;
        groupsProperty.get(0).addRepository(repository);
    }

    public void addRepositoryToGroup(String groupName, GithubRepository repository) {
        int idx = groupsProperty.indexOf(new Group(groupName));
        if (idx == -1) throw new IllegalArgumentException("Group " + groupName + " doesn't exists");
        groupsProperty.get(idx).addRepository(repository);
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
                .formatted(name, groupsProperty.stream().map(Group::getName).collect(Collectors.joining(", ")));
    }

    @Override
    public void refresh(GithubService githubService, ExecutorService executorService) {
        System.out.println("Refreshing workspace");
        groupsProperty.forEach(g -> g.refresh(githubService, executorService));
    }

    public Group getDefaultGroup() {
        return new Group("ALL");
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(new ArrayList<>(groupsProperty.getValue()));
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        groupsProperty = new SimpleListProperty<>(FXCollections.observableList((List<Group>) in.readObject()));
    }

    public void removeRepo(GithubRepository repo) {
        this.groupsProperty.forEach(group -> {
            group.getRepositories().remove(repo);
        });
    }
}
