package com.io.gittracker.model;

import com.google.gson.*;
import dev.dirs.ProjectDirectories;
import java.io.*;
import java.util.Optional;
import javafx.beans.property.ListProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermaStorage {
    private final String path;
    private final String pathSer;

    Logger logger = LoggerFactory.getLogger(PermaStorage.class);

    Gson gson;

    public PermaStorage() {
        ProjectDirectories myProjDirs = ProjectDirectories.from("com", "Ecorp", "GitTracker");
        File directory = new File(myProjDirs.configDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        path = myProjDirs.configDir + "/appState.json";
        pathSer = myProjDirs.configDir + "/appState.ser";
        logger.info("Using config path: '{}'", path);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Optional.class, new GsonOptionalDeserializer());
        gsonBuilder.registerTypeAdapter(ListProperty.class, new GsonListPropertyDeserializer());
        gson = gsonBuilder.serializeNulls().create();
    }

    public void saveState(AppState appState) {
        System.out.println(appState.getWorkspacesProperty().getValue());
        logger.debug("Saving app state...");
        File f = new File(path);
        File f2 = new File(pathSer);

        //        try (ObjectOutputStream fos = new ObjectOutputStream(new FileOutputStream(f2))) {
        //            fos.writeObject(appState);
        //        } catch (IOException e) {
        //            logger.error("Saving state failed with", e);
        //        }

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
        // TODO fix me - observable lists can't be saved to json

        try {
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            String json = new String(fis.readAllBytes());

            appState = gson.fromJson(json, AppState.class);
            return appState;

        } catch (Exception e) {
            logger.error("Loading state file failed: {}", e.getLocalizedMessage());
            return AppState.createDefault();
        }

        //        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(pathSer))) {
        //            appState = (AppState) ois.readObject();
        //            return appState;
        //        } catch (IOException | ClassNotFoundException e) {
        //            logger.error("Loading state file failed: {}", e.getLocalizedMessage());
        //        }
        //        return AppState.createDefault();
    }
}
