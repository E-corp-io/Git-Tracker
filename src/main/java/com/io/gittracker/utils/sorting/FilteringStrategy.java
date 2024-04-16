package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface FilteringStrategy extends UnaryOperator<RepositoryView[]> {
    RepositoryView[] filter(RepositoryView[] repositoryViews);

    @Override
    default RepositoryView[] apply(RepositoryView[] repositoryViews) {
        return filter(repositoryViews);
    }
}
