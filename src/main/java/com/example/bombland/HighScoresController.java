package com.example.bombland;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import org.json.JSONObject;

public class HighScoresController {
    @FXML
    StackPane highScores_stackpane;

    @FXML
    VBox highScoresPage, highScores_stackpane_child, easyHighScores_column, mediumHighScores_column, hardHighScores_column, easyHighScores_scoreContainer, mediumHighScores_scoreContainer, hardHighScores_scoreContainer;

    @FXML
    HBox highScoresContainer_bottom, highScoresContainer_leftChild, highScoresContainer_middleChild, highScoresContainer_rightChild;

    @FXML
    Label highScoresPage_title, easyHighScore_title, mediumHighScore_title, hardHighScore_title;

    @FXML
    Button backBtn;

    @FXML
    ProgressIndicator loadingIcon;


    @FXML
    public void initialize() {
        highScoresPage.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );


        backBtn.styleProperty().bind(
                Bindings.format("-fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );

        highScoresContainer_leftChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.33))
        );

        highScoresContainer_middleChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx", Main.mainStage.widthProperty().multiply(0.34))
        );

        highScoresPage_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.04))
        );

        highScoresContainer_rightChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.33))
        );


        easyHighScore_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03))
        );

        mediumHighScore_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03))
        );

        hardHighScore_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03))
        );


        VBox.setVgrow(highScores_stackpane, Priority.ALWAYS);

        VBox.setVgrow(highScoresContainer_bottom, Priority.ALWAYS);

        HBox.setHgrow(easyHighScores_column, Priority.ALWAYS);
        HBox.setHgrow(mediumHighScores_column, Priority.ALWAYS);
        HBox.setHgrow(hardHighScores_column, Priority.ALWAYS);

        VBox.setVgrow(easyHighScores_scoreContainer, Priority.ALWAYS);
        VBox.setVgrow(mediumHighScores_scoreContainer, Priority.ALWAYS);
        VBox.setVgrow(hardHighScores_scoreContainer, Priority.ALWAYS);


        if (APP_CACHE.isGettingData()) {
            displayLoadingIcon();

            Task<Void> waitTask = new Task<>() {
                @Override
                protected Void call() {
                    waitForDataRetrieval();
                    return null;
                }
            };

            waitTask.setOnSucceeded(event -> {
                hideLoadingIcon();
                displayHighScores();
            });

            new Thread(waitTask).start();
        }
        else {
            displayHighScores();
        }
    }

    private void waitForDataRetrieval() {
        while (APP_CACHE.isGettingData()) {
            // Forcing the thread to sleep for a bit in order to let it notice the
            // value of APP_CACHE.isGettingData() change
            try {
                Thread.sleep(100);  // Give some time for the state to change
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    private void displayLoadingIcon() {
        highScores_stackpane_child.setEffect(new GaussianBlur()); // blurs gameplay page
        highScores_stackpane_child.setMouseTransparent(true); // makes items in highscores page "unclickable"

        loadingIcon.setManaged(true);
        loadingIcon.setVisible(true);
    }

    private void hideLoadingIcon() {
        highScores_stackpane_child.setEffect(null);
        highScores_stackpane_child.setMouseTransparent(false); // makes items in gameplay page "clickable"

        loadingIcon.setManaged(false);
        loadingIcon.setVisible(false);
    }




    private void displayHighScores() {
        ArrayList<JSONObject> easyHighScores = APP_CACHE.getHighScores("Easy");
        ArrayList<JSONObject> mediumHighScores = APP_CACHE.getHighScores("Medium");
        ArrayList<JSONObject> hardHighScores = APP_CACHE.getHighScores("Hard");

        if (!easyHighScores.isEmpty()) {
            easyHighScores_scoreContainer.getChildren().clear();

            for (JSONObject score : easyHighScores) {
                Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
                scoreBox.getStyleClass().add("highScoreLabel");

                scoreBox.styleProperty().bind(
                        Bindings.format("-fx-font-size: %.2fpx; -fx-background-radius: %.2fpx; -fx-spacing: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02), Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.04), Main.mainStage.widthProperty().multiply(0.0075))
                );

                easyHighScores_scoreContainer.getChildren().add(scoreBox);
            }
        }

        if (!mediumHighScores.isEmpty()) {
            mediumHighScores_scoreContainer.getChildren().clear();

            for (JSONObject score : mediumHighScores) {
                Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
                scoreBox.getStyleClass().add("highScoreLabel");

                scoreBox.styleProperty().bind(
                        Bindings.format("-fx-font-size: %.2fpx; -fx-background-radius: %.2fpx; -fx-spacing: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02), Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.04), Main.mainStage.widthProperty().multiply(0.0075))
                );

                mediumHighScores_scoreContainer.getChildren().add(scoreBox);
            }
        }

        if (!hardHighScores.isEmpty()) {
            hardHighScores_scoreContainer.getChildren().clear();

            for (JSONObject score : hardHighScores) {
                Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
                scoreBox.getStyleClass().add("highScoreLabel");

                scoreBox.styleProperty().bind(
                        Bindings.format("-fx-font-size: %.2fpx; -fx-background-radius: %.2fpx; -fx-spacing: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02), Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.04), Main.mainStage.widthProperty().multiply(0.0075))
                );

                hardHighScores_scoreContainer.getChildren().add(scoreBox);
            }
        }
    }




    @FXML
    private void goToMainMenu() throws IOException {
        ScreenController screenController = new ScreenController(highScoresPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"))));
        screenController.activate("main");
    }
}
