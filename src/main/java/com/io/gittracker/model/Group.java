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

public class Group implements Serializable, Refreshable {
    private final String name;
    private transient ListProperty<GithubRepository> repositories;

    private transient Workspace workspace;

    public Group(String name) {
        this.name = name;
        repositories = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public String getName() {
        return name;
    }

    public List<GithubRepository> getRepositories() {
        return repositories.get();
    }

    public ListProperty<GithubRepository> getRepositoriesProperty() {
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
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void refresh(GithubService githubService, ExecutorService executorService) {
        System.out.println("Refreshing group");
        repositories.forEach(r -> r.refresh(githubService, executorService));
    }

    @Override
    public String toString() {
        return "Group{" + "name='"
                + name + '\'' + ", repositories="
                + repositories.stream().map(repo -> repo.getHtmlUrl()).toList() + '}';
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(new ArrayList<>(repositories.get()));
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        repositories = new SimpleListProperty<>(FXCollections.observableList((List<GithubRepository>) in.readObject()));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
