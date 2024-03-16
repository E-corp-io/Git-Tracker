package com.io.gittracker.model;

import dev.dirs.ProjectDirectories;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;

public class PermaStorage {
    private static String path;

    public PermaStorage() {
        ProjectDirectories myProjDirs = ProjectDirectories.from("com", "Ecorp", "GitTracker");
        File directory = new File(myProjDirs.configDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        path = myProjDirs.configDir + "/appState.ser";

        return;
    }

    public void saveState(AppState appState) {
        File f = new File(path);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(appState);
            oos.close();
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public AppState readState() {
        AppState appState;
        File f = new File(path);
        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            appState = (AppState) ois.readObject();
            ois.close();
        } catch (Exception e) {
            appState = new AppState(new ArrayList<Workspace>(), -1, -1, -1, new Date());
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(appState);
                oos.close();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return appState;
    }
}
