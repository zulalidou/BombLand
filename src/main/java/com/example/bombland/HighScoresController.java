package com.example.bombland;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONObject;

public class HighScoresController {
    @FXML
    VBox highScoresPage, easyHighScoresContainer, mediumHighScoresContainer, hardHighScoresContainer;

    @FXML
    HBox highScoresContainer_bottom, highScoresContainer_leftChild, highScoresContainer_middleChild, highScoresContainer_rightChild;

    @FXML
    ScrollPane easyHighScores_scrollPane, mediumHighScores_scrollPane, hardHighScores_scrollPane;

    @FXML
    VBox easyHighScores_container, mediumHighScores_container, hardHighScores_container;


    @FXML
    public void initialize() {
        HBox.setHgrow(highScoresContainer_leftChild, Priority.ALWAYS);
        HBox.setHgrow(highScoresContainer_middleChild, Priority.ALWAYS);
        HBox.setHgrow(highScoresContainer_rightChild, Priority.ALWAYS);

        HBox.setHgrow(easyHighScoresContainer, Priority.ALWAYS);
        HBox.setHgrow(mediumHighScoresContainer, Priority.ALWAYS);
        HBox.setHgrow(hardHighScoresContainer, Priority.ALWAYS);


        VBox.setVgrow(highScoresContainer_bottom, Priority.ALWAYS);
        VBox.setVgrow(easyHighScores_scrollPane, Priority.ALWAYS);
        VBox.setVgrow(mediumHighScores_scrollPane, Priority.ALWAYS);
        VBox.setVgrow(hardHighScores_scrollPane, Priority.ALWAYS);

//        VBox.setVgrow(easyHighScoresContainer, Priority.ALWAYS);
//        VBox.setVgrow(mediumHighScoresContainer, Priority.ALWAYS);
//        VBox.setVgrow(hardHighScoresContainer, Priority.ALWAYS);


        displayHighScores();
    }

    private void displayHighScores() {
        ArrayList<JSONObject> easyHighScores = APP_CACHE.getEasyHighScores();
        ArrayList<JSONObject> mediumHighScores = APP_CACHE.getMediumHighScores();
        ArrayList<JSONObject> hardHighScores = APP_CACHE.getHardHighScores();

        if (!easyHighScores.isEmpty()) {
            easyHighScores_container.getChildren().clear();

            for (JSONObject score : easyHighScores) {
                Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
                easyHighScores_container.getChildren().add(scoreBox);
            }
        }

        if (!mediumHighScores.isEmpty()) {
            mediumHighScores_container.getChildren().clear();

            for (JSONObject score : mediumHighScores) {
                Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
                mediumHighScores_container.getChildren().add(scoreBox);
            }
        }

        if (!hardHighScores.isEmpty()) {
            hardHighScores_container.getChildren().clear();

            for (JSONObject score : hardHighScores) {
                Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
                hardHighScores_container.getChildren().add(scoreBox);
            }
        }
    }

    @FXML
    private void goToMainMenu() throws IOException {
        ScreenController screenController = new ScreenController(highScoresPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/main-view.fxml")));
        screenController.activate("main");
    }
}
