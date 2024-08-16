package com.example.bombland;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

import java.io.IOException;

public class InstructionsController {
    @FXML
    Button backBtn;

    @FXML
    private void goToMainMenu(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(backBtn.getScene());
        screenController.addScreen("main", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/main-view.fxml")));
        screenController.activate("main");
    }
}
