package com.io.gittracker.model;

import com.io.gittracker.services.GithubService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class Workspace implements Serializable, Refreshable {
    private String name;
    private final List<Group> groups;

    public Workspace(String name) {
        this.name = name;

        groups = new ArrayList<>();
        groups.add(getDefaultGroup());
    }

    public String getName() {
        return name;
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public boolean checkIfWorkspaceExists(String name) {
        for (Group group : groups) {
            if (group.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public List<GithubRepository> getAllRepositories() {
        return groups.stream()
                .flatMap(group -> group.getRepositories().stream())
                .toList();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Group createNewGroup(long id, String name) {
        Group group = new Group(id, name);
        groups.add(group);
        return group;
    }

    public void addRepositoryToDefaultGroup(GithubRepository repository) {
        if (repository == null) return;
        groups.get(0).addRepository(repository);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Workspace workspace = (Workspace) o;
        return Objects.equals(name, workspace.name) && Objects.equals(groups, workspace.groups);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, groups);
    }

    @Override
    public String toString() {
        return """
                Workspace: %s
                Groups: %s
                """
                .formatted(name, groups.stream().map(Group::getName).collect(Collectors.joining(", ")));
    }

    @Override
    public void refresh(GithubService githubService, ExecutorService executorService) {
        System.out.println("Refreshing workspace");
        groups.forEach(g -> {
            g.refresh(githubService, executorService);
        });
    }

    public Group newGroup(long id, String name) {
        return new Group(id, name);
    }

    public Group getDefaultGroup() {
        return new Group(0, "DEFAULT");
    }

    public void removeRepo(GithubRepository repo) {
        this.groups.forEach(group -> {
            group.getRepositories().remove(repo);
        });
    }
}
