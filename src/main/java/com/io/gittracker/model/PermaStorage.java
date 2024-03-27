package com.io.gittracker.model;

import com.google.gson.*;
import dev.dirs.ProjectDirectories;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermaStorage {
    private final String path;

    Logger logger = LoggerFactory.getLogger(PermaStorage.class);
    Gson gson = new GsonBuilder().serializeNulls().create();

    public PermaStorage() {
        ProjectDirectories myProjDirs = ProjectDirectories.from("com", "Ecorp", "GitTracker");
        File directory = new File(myProjDirs.configDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        path = myProjDirs.configDir + "/appState.json";
        logger.info("Using config path: '{}'", path);
    }

    public void saveState(AppState appState) {
        logger.debug("Saving app state...");
        File f = new File(path);

        String json = gson.toJson(appState);
        logger.info("App state json: {}", json);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(json.getBytes());
        } catch (Exception e) { // TODO: split into exceptions
            logger.error("Saving state failed with", e);
        }

        logger.debug("App state saved");
    }

    public AppState readState() {
        logger.debug("Reading app state");

        AppState appState = new AppState();
        try {
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            String json = new String(fis.readAllBytes());

            appState = gson.fromJson(json, AppState.class);
            return appState;

        } catch (Exception e) {
            logger.error("Loading state failed with", e);
            return AppState.createDefault();
        }
    }
}
