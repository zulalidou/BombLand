package com.example.bombland;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    HBox mapSelectorContainer, highScoresContainer_bottom, highScoresContainer_leftChild, highScoresContainer_middleChild, highScoresContainer_rightChild;

    @FXML
    Label highScoresPage_title, easyHighScore_title, mediumHighScore_title, hardHighScore_title;

    @FXML
    Button backBtn, mapBtn_1, mapBtn_2, mapBtn_3, mapBtn_4;

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
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.39))
        );

        highScoresContainer_middleChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx", Main.mainStage.widthProperty().multiply(0.39))
        );

        highScoresPage_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.04))
        );

        highScoresContainer_rightChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.39))
        );


        mapSelectorContainer.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.75), Main.mainStage.widthProperty().multiply(0.01))
        );

        mapBtn_1.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-background-radius: 0px;", Main.mainStage.widthProperty().multiply(0.2425))
        );
        mapBtn_2.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-background-radius: 0px;", Main.mainStage.widthProperty().multiply(0.2425))
        );
        mapBtn_3.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-background-radius: 0px;", Main.mainStage.widthProperty().multiply(0.2425))
        );
        mapBtn_4.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-background-radius: 0px;", Main.mainStage.widthProperty().multiply(0.2425))
        );


        easyHighScore_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03),  Main.mainStage.widthProperty().multiply(0.3265))
        );

        mediumHighScore_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03),  Main.mainStage.widthProperty().multiply(0.327))
        );

        hardHighScore_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03),  Main.mainStage.widthProperty().multiply(0.3265))
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
                showRectangleMapHighScores();
            });

            new Thread(waitTask).start();
        }
        else {
            showRectangleMapHighScores();
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


    @FXML
    private void goToMainMenu() throws IOException {
        APP_CACHE.setMapOfHighScoresBeingShown("");

        ScreenController screenController = new ScreenController(highScoresPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"))));
        screenController.activate("main");
    }


    @FXML
    private void showRectangleMapHighScores() {
        if (APP_CACHE.getMapOfHighScoresBeingShown().equals("Rectangle")) {
            return;
        }

        APP_CACHE.setMapOfHighScoresBeingShown("Rectangle");
        selectMapButton("Rectangle");

        displayScores("Easy", "Rectangle", easyHighScores_scoreContainer);
        displayScores("Medium", "Rectangle", mediumHighScores_scoreContainer);
        displayScores("Hard", "Rectangle", hardHighScores_scoreContainer);
    }

    @FXML
    private void showBombMapHighScores() {
        if (APP_CACHE.getMapOfHighScoresBeingShown().equals("Bomb")) {
            return;
        }

        APP_CACHE.setMapOfHighScoresBeingShown("Bomb");

        selectMapButton("Bomb");

        displayScores("Easy", "Bomb", easyHighScores_scoreContainer);
        displayScores("Medium", "Bomb", mediumHighScores_scoreContainer);
        displayScores("Hard", "Bomb", hardHighScores_scoreContainer);
    }

    @FXML
    private void showFaceMapHighScores() {
        if (APP_CACHE.getMapOfHighScoresBeingShown().equals("Face")) {
            return;
        }

        APP_CACHE.setMapOfHighScoresBeingShown("Face");

        selectMapButton("Face");

        displayScores("Easy", "Face", easyHighScores_scoreContainer);
        displayScores("Medium", "Face", mediumHighScores_scoreContainer);
        displayScores("Hard", "Face", hardHighScores_scoreContainer);
    }

    @FXML
    private void showFlowerMapHighScores() {
        if (APP_CACHE.getMapOfHighScoresBeingShown().equals("Flower")) {
            return;
        }

        APP_CACHE.setMapOfHighScoresBeingShown("Flower");

        selectMapButton("Flower");

        displayScores("Easy", "Flower", easyHighScores_scoreContainer);
        displayScores("Medium", "Flower", mediumHighScores_scoreContainer);
        displayScores("Hard", "Flower", hardHighScores_scoreContainer);
    }

    private void selectMapButton(String map) {
        if (map.equals("Rectangle")) {
            if (mapBtn_1.getStyleClass().contains("mapBtn_selected")) {
                return;
            }

            mapBtn_1.getStyleClass().remove("mapBtn_unselected");
            mapBtn_1.getStyleClass().add("mapBtn_selected");

            deselectMapButton(mapBtn_2);
            deselectMapButton(mapBtn_3);
            deselectMapButton(mapBtn_4);
        }
        else if (map.equals("Bomb")) {
            mapBtn_2.getStyleClass().remove("mapBtn_unselected");
            mapBtn_2.getStyleClass().add("mapBtn_selected");

            deselectMapButton(mapBtn_1);
            deselectMapButton(mapBtn_3);
            deselectMapButton(mapBtn_4);
        }
        else if (map.equals("Face")) {
            mapBtn_3.getStyleClass().remove("mapBtn_unselected");
            mapBtn_3.getStyleClass().add("mapBtn_selected");

            deselectMapButton(mapBtn_1);
            deselectMapButton(mapBtn_2);
            deselectMapButton(mapBtn_4);
        }
        else {
            mapBtn_4.getStyleClass().remove("mapBtn_unselected");
            mapBtn_4.getStyleClass().add("mapBtn_selected");

            deselectMapButton(mapBtn_1);
            deselectMapButton(mapBtn_2);
            deselectMapButton(mapBtn_3);
        }
    }

    private void deselectMapButton(Button button) {
        if (button.getStyleClass().contains("mapBtn_selected")) {
            button.getStyleClass().remove("mapBtn_selected");
            button.getStyleClass().add("mapBtn_unselected");
        }
    }

    private void displayScores(String difficulty, String map, VBox container) {
        container.getChildren().clear();

        ArrayList<JSONObject> easyHighScores = getMapScores(difficulty, map);

        if (!easyHighScores.isEmpty()) {
            addScoresToScreen(easyHighScores, container);
        }
        else {
            displayPlaceholder(container);
        }
    }

    private ArrayList<JSONObject> getMapScores(String difficulty, String map) {
        final ArrayList<JSONObject> allHighScores = APP_CACHE.getHighScores(difficulty);
        ArrayList<JSONObject> mapHighScores = new ArrayList<>();

        for (int i = 0; i < allHighScores.size(); i++) {
            if (allHighScores.get(i).getString("map").equals(map)) {
                mapHighScores.add(allHighScores.get(i));
            }
        }

        return mapHighScores;
    }

    private void addScoresToScreen(ArrayList<JSONObject> scores, VBox scoresContainer) {
        for (JSONObject score : scores) {
            Label scoreBox = new Label(score.getString("name") + ", " + score.getLong("score"));
            scoreBox.getStyleClass().add("highScoreLabel");

            scoreBox.styleProperty().bind(
                    Bindings.format("-fx-font-size: %.2fpx; -fx-background-radius: %.2fpx; -fx-spacing: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02), Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.04), Main.mainStage.widthProperty().multiply(0.0075))
            );

            scoresContainer.getChildren().add(scoreBox);
        }
    }

    private void displayPlaceholder(VBox scoresContainer) {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bombland/Images/smiley-face.png")));
        ImageView imgView = new ImageView();
        imgView.setImage(img);
        imgView.setFitWidth(90);
        imgView.setFitHeight(60);
        imgView.setPreserveRatio(true);
        scoresContainer.getChildren().add(imgView);

        Label noActivityLabel = new Label("No activity yet!");
        noActivityLabel.getStyleClass().add("highScores_activityLabel");
        scoresContainer.getChildren().add(noActivityLabel);
    }
}
