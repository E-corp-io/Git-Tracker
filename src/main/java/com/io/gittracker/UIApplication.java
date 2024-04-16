package com.io.gittracker;

import com.io.gittracker.services.AppStateService;
import com.io.gittracker.utils.StageReadyEvent;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class UIApplication extends Application {
    private ConfigurableApplicationContext context;
    private HostServices hostServices = getHostServices();

    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer = applicationContext -> {
            applicationContext.registerBean(Application.class, () -> UIApplication.this);
        };
        // this.hostServices = getHostServices();
        this.context = new SpringApplicationBuilder(GitTrackerApplication.class)
                .initializers(initializer)
                .run();
    }

    @Bean
    public HostServices getGetHostServices() {
        return this.hostServices;
    }

    @Override
    public void start(Stage primaryStage) {
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        AppStateService service = context.getBean(AppStateService.class);
        System.out.println("Saving app state...");
        service.saveAppState();
        context.close();
        Platform.exit();
    }
}
