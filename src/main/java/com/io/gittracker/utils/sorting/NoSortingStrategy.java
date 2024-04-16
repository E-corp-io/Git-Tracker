package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;

public class NoSortingStrategy implements SortingStrategy {
    @Override
    public RepositoryView[] sort(RepositoryView[] repositoryViews) {
        return repositoryViews;
    }
}
