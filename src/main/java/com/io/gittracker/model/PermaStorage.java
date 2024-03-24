package com.io.gittracker.model;

import dev.dirs.ProjectDirectories;
import java.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermaStorage {
    private final String path;

    Logger logger = LoggerFactory.getLogger(PermaStorage.class);

    public PermaStorage() {
        ProjectDirectories myProjDirs = ProjectDirectories.from("com", "Ecorp", "GitTracker");
        File directory = new File(myProjDirs.configDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        path = myProjDirs.configDir + "/appState.ser";
    }

    public void saveState(AppState appState) {
        logger.debug("Saving app state");
        File f = new File(path);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(appState);
        } catch (IOException ex) {
            logger.error("failed to save app state {}", appState, ex);
            throw new RuntimeException(ex);
        }
    }

    public AppState readState() {
        logger.debug("Reading app state");
        AppState appState;
        File f = new File(path);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            appState = (AppState) ois.readObject();
        } catch (Exception e) {
            logger.error("failed to read app state, creating new state...");
            appState = new AppState();
            saveState(appState);
        }
        return appState;
    }
}
