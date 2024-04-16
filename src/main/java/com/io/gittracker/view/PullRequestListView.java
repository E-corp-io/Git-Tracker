package com.io.gittracker.view;

import com.io.gittracker.model.PullRequest;
import java.util.function.Predicate;
import javafx.application.HostServices;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PullRequestListView extends VBox {
    private final PullRequest pr;

    public PullRequestListView(PullRequest pr, HostServices hs) {
        this.pr = pr;
        Label prLabel = new Label(pr.getTitle() + " | " + pr.getUpdatedAtDate());
        prLabel.getStyleClass().add("clickable");
        prLabel.getStyleClass().add("pr-title");
        getStyleClass().add("pr-border");

        prLabel.setOnMouseClicked(event -> {
            hs.showDocument(pr.getHtmlURL().toString());
            event.consume();
        });
        getChildren().add(prLabel);
        getChildren()
                .addAll(pr.getComments().stream()
                        .map(prComment -> {
                            Label label = new Label(prComment.username() + " commented: "
                                    + (prComment.body().length() > 200
                                            ? prComment.body().substring(0, 197) + "[...]"
                                            : prComment.body()));
                            label.setWrapText(true);
                            return label;
                        })
                        .toList());
    }

    public void filterSelf(Predicate<PullRequest> predicate) {
        boolean shouldBeVisible = predicate.test(pr);
        setVisible(shouldBeVisible);
        setManaged(shouldBeVisible);
    }
}
