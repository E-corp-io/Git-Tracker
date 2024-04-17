package com.io.gittracker.controllers;

import com.io.gittracker.UIMain;
import javafx.scene.input.MouseEvent;
import org.springframework.stereotype.Component;

@Component
public class Settings {
    private final UIMain uiMain;

    Settings(UIMain uiMain) {
        this.uiMain = uiMain;
    }

    public void openMainView(MouseEvent mouseEvent) {
        uiMain.loadSubjectView();
    }
}
