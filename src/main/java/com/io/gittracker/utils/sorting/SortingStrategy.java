package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface SortingStrategy extends UnaryOperator<RepositoryView[]> {
    RepositoryView[] sort(RepositoryView[] repositoryViews);

    @Override
    default RepositoryView[] apply(RepositoryView[] repositoryViews) {
        return sort(repositoryViews);
    }
}
