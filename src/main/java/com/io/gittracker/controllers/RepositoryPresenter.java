package com.io.gittracker.controllers;

import com.io.gittracker.model.GithubRepository;
import com.io.gittracker.model.PullRequest;
import java.util.Comparator;
import java.util.List;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
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
    private TitledPane rootPane;

    @FXML
    private GridPane gridPane;

    @FXML
    private VBox pullRequestsVBox;

    private HostServices hostServices;

    @Autowired
    private void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    public void initialize(GithubRepository repository, Region parent) {
        rootPane.setAnimated(false);
        repositoryNameLabel.setText(repository.getName());

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
        List<String> prNames =
                newPullRequests.stream().map(PullRequest::getTitle).toList();
        pullRequestsVBox.getChildren().clear();
        pullRequestsVBox
                .getChildren()
                .addAll(newPullRequests.stream()
                        .map(pr -> {
                            VBox prVbox = new VBox();
                            Label prLabel = new Label(pr.getTitle() + " | " + pr.getUpdatedAtDate());
                            prLabel.getStyleClass().add("clickable");
                            prLabel.getStyleClass().add("pr-title");
                            prVbox.getStyleClass().add("pr-border");

                            prLabel.setOnMouseClicked(event -> {
                                this.hostServices.showDocument(pr.getHtmlURL().toString());
                                event.consume();
                            });
                            prVbox.getChildren().add(prLabel);
                            prVbox.getChildren()
                                    .addAll(pr.getComments().stream()
                                            .map(prComment -> {
                                                Label label = new Label(
                                                        prComment.username() + " commented: " + prComment.body());
                                                label.setWrapText(true);
                                                return label;
                                            })
                                            .toList());
                            return prVbox;
                        })
                        .toList());
        newPrCountLabel.setText(String.valueOf(prNames.size()));
        String latestDate = String.valueOf(newPullRequests.stream()
                .max(Comparator.comparing(PullRequest::getUpdatedAtDate))
                .get()
                .getUpdatedAtDate());
        lastPrDateLabel.setText(latestDate);
    }
}
