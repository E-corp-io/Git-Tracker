package com.io.gittracker;

import com.io.gittracker.utils.StageReadyEvent;
import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        stage.setTitle("Git Tracker");
        Scene scene;
        try {
            scene = this.loadTokenInput();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(scene);
        stage.show();
    }

    private Scene loadTokenInput() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/tokenInput.fxml"));
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/styles/tokenInput.css"))
                        .toExternalForm());
        return scene;
    }
}
