package com.io.gittracker;

import com.io.gittracker.utils.StageReadyEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class UIApplication extends Application {
    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer = applicationContext -> {
            applicationContext.registerBean(Application.class, () -> UIApplication.this);
        };
        this.context = new SpringApplicationBuilder(GitTrackerApplication.class)
                .initializers(initializer)
                .run();
    }

    @Override
    public void start(Stage primaryStage) {
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
