package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;

public class NoFilteringStrategy implements FilteringStrategy {
    @Override
    public RepositoryView[] filter(RepositoryView[] repositoryViews) {
        return repositoryViews;
    }
}
