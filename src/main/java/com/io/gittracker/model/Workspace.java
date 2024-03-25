package com.io.gittracker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    public List<GithubRepository> getAllRepositories() {
        return groups.stream()
                .flatMap(group -> group.getRepositories().stream())
                .toList();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public Group createNewGroup(long id, String name) {
        Group group = new Group(id, name, this);
        groups.add(group);
        return group;
    }

    public void addRepositoryToDefaultGroup(GithubRepository repository) {
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
    public void refresh() {
        groups.forEach(Group::refresh);
    }

    public Group newGroup(long id, String name) {
        return new Group(id, name, this);
    }

    public Group getDefaultGroup() {
        return new Group(0, "DEFAULT", this);
    }

    public class Group implements Refreshable, Serializable {
        private final long id;
        private String name;
        private final List<GithubRepository> repositories;

        Workspace workspace;

        public Group(long id, String name, Workspace workspace) {
            this.id = id;
            this.name = name;
            this.workspace = workspace;
            repositories = new ArrayList<>();
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<GithubRepository> getRepositories() {
            return repositories;
        }

        public void addRepository(GithubRepository githubRepository) {
            repositories.add(githubRepository);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Group that = (Group) o;
            return id == that.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public void refresh() {
            repositories.forEach(Refreshable::refresh);
        }
    }
}
