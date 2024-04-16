package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;
import java.util.Arrays;
import java.util.Date;

public class PrDateSortingStrategy implements SortingStrategy {
    @Override
    public RepositoryView[] sort(RepositoryView[] repositoryViews) {
        return Arrays.stream(repositoryViews)
                .sorted((o1, o2) -> {
                    if (o1.getRepository().getLatestPullRequest().isEmpty()) return 1;
                    if (o2.getRepository().getLatestPullRequest().isEmpty()) return -1;
                    Date d1 = o1.getRepository().getLatestPullRequest().get().getUpdatedAtDate();
                    Date d2 = o2.getRepository().getLatestPullRequest().get().getUpdatedAtDate();
                    return d1.compareTo(d2);
                })
                .toArray(RepositoryView[]::new);
    }
}
