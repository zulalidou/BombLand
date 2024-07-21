package com.example.bombland;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class PlayController {
    @FXML
    VBox pageContainer;

    @FXML
    private void goToMainMenu(ActionEvent event) throws IOException {
        Utility.changePage(getClass(), event, "main-view.fxml");
    }

    @FXML
    public void initialize() {
        System.out.println("Hello from initialize()");
        GridPane grid = new GridPane();

        // Creates a grid of 8 rows and 10 columns
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                Button tile = new Button();
                grid.add(tile, col, row, 1, 1);
            }
        }

        pageContainer.getChildren().add(grid);
    }
}