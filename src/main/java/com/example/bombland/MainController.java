package com.example.bombland;

import javafx.application.Platform;
import javafx.fxml.FXML;

public class MainController {
    @FXML
    private void closeApp() {
        Platform.exit();
    }
}