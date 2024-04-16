package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;
import java.util.Arrays;

public class RepoNameFilteringStrategy implements FilteringStrategy {

    private final String repoNameQuery;

    public RepoNameFilteringStrategy(String repoNameQuery) {
        this.repoNameQuery = repoNameQuery;
    }

    @Override
    public RepositoryView[] filter(RepositoryView[] repositoryViews) {
        return Arrays.stream(repositoryViews)
                .filter(repositoryView ->
                        repositoryView.getRepository().getName().toLowerCase().contains(repoNameQuery.toLowerCase()))
                .toArray(RepositoryView[]::new);
    }
}
