package com.io.gittracker.utils.sorting;

import com.io.gittracker.view.RepositoryView;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang3.ArrayUtils;

public class FilteringAndSortingPipeline {
    private SortingStrategy sortingStrategy = new NoSortingStrategy();
    private UnaryOperator<RepositoryView[]> reverseSortingStrategy = UnaryOperator.identity();
    private FilteringStrategy filteringStrategy = new NoFilteringStrategy();
    private FilteringStrategy groupFilteringStrategy = new NoFilteringStrategy();
    private final ObjectProperty<Function<RepositoryView[], RepositoryView[]>> sortingAndFilteringProperty =
            new SimpleObjectProperty<>(new NoFilteringStrategy());

    public ObjectProperty<Function<RepositoryView[], RepositoryView[]>> getSortingAndFilteringProperty() {
        return sortingAndFilteringProperty;
    }

    public ObjectProperty<Function<RepositoryView[], RepositoryView[]>> sortingAndFilteringPropertyProperty() {
        return sortingAndFilteringProperty;
    }

    public void setSortingStrategy(SortingStrategy sortingStrategy) {
        this.sortingStrategy = sortingStrategy;
        refresh();
    }

    public void setReverseSortingStrategy(boolean reverseSort) {
        if (reverseSort) {
            this.reverseSortingStrategy = reps -> {
                RepositoryView[] views = sortingStrategy.sort(filteringStrategy.filter(reps));
                ArrayUtils.reverse(views);
                return views;
            };
        } else {
            this.reverseSortingStrategy = UnaryOperator.identity();
        }
        refresh();
    }

    public void setFilteringStrategy(FilteringStrategy filteringStrategy) {
        this.filteringStrategy = filteringStrategy;
        refresh();
    }

    public void setGroupFilteringStrategy(FilteringStrategy groupFilteringStrategy) {
        this.groupFilteringStrategy = groupFilteringStrategy;
        refresh();
    }

    public void refresh() {
        sortingAndFilteringProperty.set(filteringStrategy
                .andThen(groupFilteringStrategy)
                .andThen(sortingStrategy)
                .andThen(reverseSortingStrategy));
    }

    public void clearAllFilters() {
        sortingStrategy = new NoSortingStrategy();
        reverseSortingStrategy = UnaryOperator.identity();
        filteringStrategy = new NoFilteringStrategy();
        groupFilteringStrategy = new NoFilteringStrategy();
        refresh();
    }
}
