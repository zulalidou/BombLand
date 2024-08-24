package com.example.bombland;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class InstructionsController {
    @FXML
    VBox instructionsPage, instructionsContainer;

    @FXML
    HBox instructionsContainer_leftChild, instructionsContainer_middleChild, instructionsContainer_rightChild;

    @FXML
    ScrollPane instructionsScrollPane;

    @FXML
    public void initialize() {
        HBox.setHgrow(instructionsContainer_leftChild, Priority.ALWAYS);
        HBox.setHgrow(instructionsContainer_middleChild, Priority.ALWAYS);
        HBox.setHgrow(instructionsContainer_rightChild, Priority.ALWAYS);

        // Makes sure the child of the scrollpane has the same width as the scrollpane
        instructionsScrollPane.setFitToWidth(true);
        VBox.setVgrow(instructionsScrollPane, Priority.ALWAYS);
    }

    @FXML
    private void goToMainMenu(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(instructionsPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/main-view.fxml")));
        screenController.activate("main");
    }
}
