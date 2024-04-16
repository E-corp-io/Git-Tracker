package com.io.gittracker.view;

import static com.io.gittracker.model.Workspace.DEFAULT_GROUP;

import com.io.gittracker.model.PullRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Accordion;

public class WorkspaceView extends Accordion {
    private List<RepositoryView> repositoryViews;
    private String visibleGroupName = DEFAULT_GROUP;

    private final ObjectProperty<Function<RepositoryView[], RepositoryView[]>> sortAndFilter;

    public WorkspaceView(
            RepositoryView[] repositoryViews,
            ObjectProperty<Function<RepositoryView[], RepositoryView[]>> sortAndFilter) {
        this.repositoryViews = new ArrayList<>(Arrays.asList(repositoryViews));
        this.sortAndFilter = sortAndFilter;
        this.sortAndFilter.addListener((observableValue, oldFunc, newFunc) -> sortAndFilterRepos(newFunc));
        sortAndFilterRepos(sortAndFilter.get());
        prefHeight(0);
    }

    public void sortAndFilterRepos(Function<RepositoryView[], RepositoryView[]> func) {
        getPanes().clear();
        getPanes().addAll(func.apply(repositoryViews.toArray(RepositoryView[]::new)));
    }

    public void setVisibleGroup(String selectedGroupName) {
        visibleGroupName = selectedGroupName;
    }

    public String getVisibleGroup() {
        return visibleGroupName;
    }

    public void addRepositories(RepositoryView[] repositoryViews) {
        this.repositoryViews.addAll(List.of(repositoryViews));
        sortAndFilterRepos(sortAndFilter.get());
    }

    public void filterPrRequests(Predicate<PullRequest> predicate) {
        repositoryViews.forEach(repoView -> repoView.filterPrs(predicate));
    }
}
