package com.example.bombland;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class MainController {
    @FXML
    private void openInstructionsPage(ActionEvent event) throws IOException {
        Utility.changePage(getClass(), event, "instructions-view.fxml");
    }

    @FXML
    private void closeApp() {
        Platform.exit();
    }
}