package com.example.bombland;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class InstructionsController {
    @FXML
    private void goToMainMenu(ActionEvent event) throws IOException {
        Utility.changePage(getClass(), event, "main-view.fxml");
    }
}
