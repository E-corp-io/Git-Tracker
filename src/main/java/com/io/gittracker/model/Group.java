package com.io.gittracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Group implements Refreshable {
    private final long id;
    private String name;
    private final List<GithubRepository> repositories;

    // Workspace workspace;

    public Group(long id, String name) {
        this.id = id;
        this.name = name;
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
