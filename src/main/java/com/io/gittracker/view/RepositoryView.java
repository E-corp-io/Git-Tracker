package com.io.gittracker.view;

import com.io.gittracker.controllers.RepositoryPresenter;
import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.PullRequest;
import java.util.function.Predicate;
import javafx.scene.control.TitledPane;

public class RepositoryView extends TitledPane {
    private String groupName;
    private GithubRepository repository;
    private RepositoryPresenter repositoryPresenter;

    public void setRepository(GithubRepository githubRepository) {
        this.repository = githubRepository;
        this.groupName = this.repository.getGroupName();
    }

    public String getGroupName() {
        return groupName;
    }

    public GithubRepository getRepository() {
        return this.repository;
    }

    public void setPresenter(RepositoryPresenter repositoryPresenter) {
        this.repositoryPresenter = repositoryPresenter;
    }

    public void filterPrs(Predicate<PullRequest> predicate) {
        repositoryPresenter.filterPullRequest(predicate);
    }
}
