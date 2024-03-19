package com.io.gittracker;

import com.io.gittracker.utils.StageReadyEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UIMain implements ApplicationListener<StageReadyEvent> {
    private final ConfigurableApplicationContext applicationContext;

    public UIMain(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    private final BorderPane rootPane = new BorderPane();

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setTitle("Git Tracker");
        Scene scene = new Scene(rootPane);
        this.loadTokenInput();
        stage.setScene(scene);
        stage.show();
    }

    private void loadTokenInput() {
        this.load("/fxml/tokenInput.fxml","/styles/tokenInput.css");
    }
    public void loadSubjectView() {
        this.load("/fxml/mainView.fxml", "/styles/mainView.css");
    }
    private void load(String fxml, String css) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        Pane view = null;
        try {
            view = fxmlLoader.load();
        }catch (IOException e){
            System.out.printf("fxml loader failed loading '%s'\n",fxml);
        }
        Optional.ofNullable(view).ifPresent(
                v -> v.getStylesheets().add(Objects.requireNonNull(getClass().getResource(css)).toExternalForm()))
        ;
        rootPane.setCenter(view);
    }
}
