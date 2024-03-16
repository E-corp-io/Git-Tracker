package com.io.gittracker;

import com.io.gittracker.model.AppState;
import com.io.gittracker.model.PermaStorage;
import com.io.gittracker.model.Workspace;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PermaStorageTest {

    @Test
    void testSaveAndLoad() {
        List<Workspace> workspaces = new ArrayList<>();
        workspaces.add(new Workspace("test", new ArrayList<>()));
        AppState appState = new AppState(workspaces, -1, -1, -1, null);
        PermaStorage permaStorage = new PermaStorage();
        permaStorage.saveState(appState);
        AppState readState = permaStorage.readState();
        assert (readState.equals(appState));
    }
}
