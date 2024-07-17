package com.example.bombland;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Utility {
    @FXML
    protected static void changePage(Class currentClass, Event event, String FXML_fileName) throws IOException {
        Parent root = FXMLLoader.load(currentClass.getResource(FXML_fileName));
        Scene scene = new Scene(root, 800, 500);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
