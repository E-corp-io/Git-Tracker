package com.io.gittracker.utils.sorting;

import static com.io.gittracker.model.Workspace.DEFAULT_GROUP;

import com.io.gittracker.view.RepositoryView;
import java.util.Arrays;
import java.util.Objects;

public class GroupFilteringStrategy implements FilteringStrategy {
    private final String groupName;

    public GroupFilteringStrategy(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public RepositoryView[] filter(RepositoryView[] repositoryViews) {
        if (Objects.equals(groupName, DEFAULT_GROUP)) return repositoryViews;
        return Arrays.stream(repositoryViews)
                .filter(repositoryView ->
                        repositoryView.getRepository().getGroupName().equals(groupName))
                .toArray(RepositoryView[]::new);
    }
}
