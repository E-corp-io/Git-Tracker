package com.io.gittracker.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class Workspace implements Serializable {
    private String name;
    private final List<Repository> repositories;

    public Workspace(String name, List<Repository> repositories) {
        this.name = name;
        this.repositories = repositories;
    }

    public String getName() {
        return name;
    }

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Workspace) obj;
        return Objects.equals(this.name, that.name) && Objects.equals(this.repositories, that.repositories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, repositories);
    }

    @Override
    public String toString() {
        return "Workspace[" + "name=" + name + ", " + "repositories=" + repositories + ']';
    }
}
