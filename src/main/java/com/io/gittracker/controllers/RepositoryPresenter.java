package com.io.gittracker.controllers;

import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.PullRequest;
import com.io.gittracker.view.PullRequestListView;
import com.io.gittracker.view.RepositoryView;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.function.Predicate;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class RepositoryPresenter {

    @FXML
    private Label repositoryNameLabel;

    @FXML
    private Label lastPrDateLabel;

    @FXML
    private Label newPrCountLabel;

    @FXML
    private Label groupLabel;

    @FXML
    private RepositoryView rootPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private VBox pullRequestsVBox;

    private HostServices hostServices;

    private String repoName;

    private int totalPrCount = 0;
    // TODO total pr count is not that useful, maybe change it to unseen prs?
    private int visiblePrCount = 0;

    @Autowired
    private void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize(GithubRepository repository, Region parent) {
        rootPane.setAnimated(false);
        rootPane.setExpanded(false);
        rootPane.setPresenter(this);
        this.repoName = repository.getName();
        groupLabel.setText(rootPane.getGroupName());
        repositoryNameLabel.setText(this.repoName);

        // Bind the prefWidth of the GridPane to the prefWidth of the TitledPane
        gridPane.prefWidthProperty().bind(parent.widthProperty().subtract(100));

        repositoryNameLabel.getStyleClass().add("clickable");
        repositoryNameLabel.setOnMouseClicked(event -> {
            this.hostServices.showDocument(repository.getHtmlUrl().toString());
            // TODO fix me - clicking the link should not open the pane
            event.consume();
        });

        repository.getPullRequestsProperty().addListener((observableValue, oldPullRequests, newPullRequests) -> {
            updatePullRequestsView(newPullRequests);
        });
        updatePullRequestsView(repository.getPullRequestsProperty());
    }

    private void updatePullRequestsView(ObservableList<PullRequest> newPullRequests) {
        if (newPullRequests == null || newPullRequests.isEmpty()) return;
        pullRequestsVBox.getChildren().clear();
        pullRequestsVBox
                .getChildren()
                .addAll(newPullRequests.stream()
                        .sorted(Comparator.comparing(PullRequest::getUpdatedAtDate)
                                .reversed())
                        .map(pr -> new PullRequestListView(pr, hostServices))
                        .toList());

        newPrCountLabel.setText(String.valueOf(pullRequestsVBox.getChildren().size()));
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String latestDate = format.format(newPullRequests.stream()
                .max(Comparator.comparing(PullRequest::getUpdatedAtDate))
                .get()
                .getUpdatedAtDate());
        lastPrDateLabel.setText(latestDate);
        filterPullRequest(prevPredicate);
    }

    Predicate<PullRequest> prevPredicate = pr -> true;

    public void filterPullRequest(Predicate<PullRequest> predicate) {
        this.prevPredicate = predicate;
        pullRequestsVBox.getChildren().forEach(node -> {
            if (node instanceof PullRequestListView prView) {
                prView.filterSelf(predicate);
            }
        });
        visiblePrCount =
                pullRequestsVBox.getChildren().filtered(Node::isVisible).size();
        totalPrCount = pullRequestsVBox.getChildren().size();
        if (totalPrCount == 0) {
            newPrCountLabel.setText("-");
        } else {
            newPrCountLabel.setText("%s / %s".formatted(visiblePrCount, totalPrCount));
        }
    }

    public String getRepoName() {
        return this.repoName;
    }
}
