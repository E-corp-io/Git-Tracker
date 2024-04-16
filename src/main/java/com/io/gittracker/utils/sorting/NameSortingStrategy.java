package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;
import java.util.Arrays;
import java.util.Comparator;

public class NameSortingStrategy implements SortingStrategy {
    @Override
    public RepositoryView[] sort(RepositoryView[] repositoryViews) {
        return Arrays.stream(repositoryViews)
                .sorted(Comparator.comparing(o -> o.getRepository().getName()))
                .toArray(RepositoryView[]::new);
    }
}
