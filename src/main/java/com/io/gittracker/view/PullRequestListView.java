package com.io.gittracker.view;

import com.io.gittracker.model.PullRequest;
import java.text.SimpleDateFormat;
import java.util.function.Predicate;
import javafx.application.HostServices;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PullRequestListView extends VBox {
    private final PullRequest pr;

    public PullRequestListView(PullRequest pr, HostServices hs) {
        this.pr = pr;
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        HBox line = new HBox();
        Label prLabel = new Label(pr.getTitle());
        prLabel.getStyleClass().add("clickable");
        prLabel.getStyleClass().add("pr-title");
        getStyleClass().add("pr-border");

        Label dateLabel = new Label(" | " + format.format(pr.getUpdatedAtDate()));
        line.getChildren().add(prLabel);
        line.getChildren().add(dateLabel);

        prLabel.setOnMouseClicked(event -> {
            hs.showDocument(pr.getHtmlURL().toString());
            event.consume();
        });
        getChildren().add(line);
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
