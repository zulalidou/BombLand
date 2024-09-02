package com.example.bombland;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ModeSelectionController {
    @FXML
    VBox modeSelectionPage;

    @FXML
    private void goToMainMenu(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(modeSelectionPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/main-view.fxml")));
        screenController.activate("main");
    }

    @FXML
    private void openEasyMode() throws IOException {
        PlayController.setMode("EASY");
        startGame();
    }

    @FXML
    private void openMediumMode() throws IOException {
        PlayController.setMode("MEDIUM");
        startGame();
    }

    @FXML
    private void openHardMode() throws IOException {
        PlayController.setMode("HARD");
        startGame();
    }

    @FXML
    private void startGame() throws IOException {
        ScreenController screenController = new ScreenController(modeSelectionPage.getScene());
        screenController.addScreen("play", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/play-view.fxml")));
        screenController.activate("play");
    }
}
