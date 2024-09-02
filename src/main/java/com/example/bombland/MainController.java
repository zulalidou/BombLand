package com.example.bombland;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {
    @FXML
    VBox mainMenuPage;

    @FXML
    public void initialize() {
        // Prevents the width of the mainMenuOptionsContainer VBox from having the same width as its parent container (mainMenuPage)
        mainMenuPage.setFillWidth(false);
    }

    @FXML
    private void openModeSelectionPage(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(mainMenuPage.getScene());
        screenController.addScreen("mode-selection", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/mode-selection-view.fxml")));
        screenController.activate("mode-selection");
    }

    @FXML
    private void openInstructionsPage(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(mainMenuPage.getScene());
        screenController.addScreen("instructions", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/instructions-view.fxml")));
        screenController.activate("instructions");
    }

    @FXML
    private void closeApp() {
        Platform.exit();
    }
}