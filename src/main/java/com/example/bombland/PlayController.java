package com.example.bombland;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.media.AudioClip;
import org.json.JSONObject;
import java.util.UUID;

public class PlayController {
    private static PlayController instance;
    private boolean gameStarted, gameLost;
    private long gameDuration;
    private ScheduledExecutorService  taskScheduler;
    private boolean timerPaused;


    @FXML
    StackPane playPageContainer_inner;

    @FXML
    VBox playPageContainer, stackpane_child1, emptySpace, gridContainer, exitPagePopup, exitPagePopup_imgContainer, gameLostPopup, gameLostPopup_imgContainer, gameWonPopup, gameWonPopup_imgContainer, newRecordPopup, newRecordPopup_imgContainer;

    @FXML
    Label totalBombsLbl, timeElapsedLbl, flagsLeftLbl, exitPagePopup_title, exitPagePopup_text, gameLostPopup_title, gameLostPopup_timeTaken, gameWonPopup_title, gameWonPopup_timeTaken, newRecordPopup_title, newRecordPopup_timeTaken, newRecordPopup_text, playerName_error;

    @FXML
    Button backBtn, exitPagePopup_cancelBtn, exitPagePopup_mainMenuBtn, gameLostPopup_playAgainBtn, gameLostPopup_mainMenuBtn, gameWonPopup_playAgainBtn, gameWonPopup_mainMenuBtn, newRecordPopup_playAgainBtn;

    @FXML
    HBox backBtnContainer, exitPagePopup_buttonsContainer, gameLostPopup_buttonsContainer, gameWonPopup_buttonsContainer, newRecordPopup_buttonsContainer, playerInfo_hbox, playPageContainer_header;

    @FXML
    TextField playerName_textField;

    @FXML
    ImageView exitPagePopup_img, gameLostPopup_img, gameWonPopup_img, newRecordPopup_img;


    private PlayController() {}

    public static PlayController getInstance() {
        if (instance == null) {
            instance = new PlayController();
        }

        return instance;
    }


    public boolean getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }


    public boolean getGameLost() {
        return gameLost;
    }

    public void setGameLost(boolean gameLost) {
        this.gameLost = gameLost;
    }

    public void setFlagsLeftLbl(String flagsLeftText) {
        flagsLeftLbl.setText(flagsLeftText);
    }

    public void setGridContainer(GridPane grid) {
        gridContainer.getChildren().add(grid);
    }


    @FXML
    private void closeExitPagePopup() {
        exitPagePopup.setManaged(false);
        exitPagePopup.setVisible(false);

        stackpane_child1.setEffect(null);
        stackpane_child1.setMouseTransparent(false); // makes items in gameplay page "clickable"

        timerPaused = false;
    }


    @FXML
    private void goToMainMenu() throws IOException {
        endTimer();

        ScreenController screenController = new ScreenController(playPageContainer.getScene());
        screenController.removeScreen("play");

        screenController.addScreen("main", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"))));
        screenController.activate("main");
    }


    @FXML
    private void verifyExitPage() throws IOException {
        if (!gameStarted) {
            goToMainMenu();
            return;
        }

        timerPaused = true;

        stackpane_child1.setEffect(new GaussianBlur()); // blurs gameplay page
        stackpane_child1.setMouseTransparent(true); // makes items in gameplay page "unclickable"

        exitPagePopup.setManaged(true);
        exitPagePopup.setVisible(true);

        exitPagePopup.setMaxWidth(Main.mainStage.widthProperty().get() * 0.4);
        exitPagePopup.setMaxHeight(Main.mainStage.heightProperty().get() * 0.4);
        exitPagePopup.setStyle("-fx-background-radius: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        exitPagePopup_title.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        exitPagePopup_imgContainer.setStyle("-fx-pref-height: " + (Main.mainStage.getHeight() * 0.1) + "px; -fx-padding: " + (Main.mainStage.getHeight() * 0.03) + " 0 0 0;");
        exitPagePopup_img.setFitWidth(Main.mainStage.getWidth() * 0.1);
        exitPagePopup_img.setFitHeight(Main.mainStage.getWidth() * 0.1);

        exitPagePopup_text.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.025) + "px;");

        VBox.setVgrow(exitPagePopup_buttonsContainer, Priority.ALWAYS);

        exitPagePopup_buttonsContainer.setSpacing(Main.mainStage.getWidth() * 0.05);
        exitPagePopup_cancelBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
        exitPagePopup_mainMenuBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
    }


    @FXML
    public void initialize() {
        VBox.setVgrow(playPageContainer_inner, Priority.ALWAYS);

        playPageContainer_header.styleProperty().bind(
                Bindings.format("-fx-pref-height: %.2fpx;", Main.mainStage.heightProperty().multiply(0.125))
        );

        backBtnContainer.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );

        backBtn.styleProperty().bind(
                Bindings.format("-fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );


        totalBombsLbl.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.3), Main.mainStage.widthProperty().multiply(0.035))
        );

        timeElapsedLbl.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.3), Main.mainStage.widthProperty().multiply(0.035))
        );

        flagsLeftLbl.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.3), Main.mainStage.widthProperty().multiply(0.035))
        );

        emptySpace.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );


        playPageContainer_header.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.875))
        );


        buildGrid();
    }


    public void buildGrid() {
        gameStarted = gameLost = false;
        gameDuration = -1;
        taskScheduler = Executors.newScheduledThreadPool(1);
        timerPaused = false;
        timeElapsedLbl.setText("0 seconds");

        GameMap.getInstance().buildGrid();

        final int bombs = GameMap.getInstance().getBombs();
        totalBombsLbl.setText(bombs + " bombs");
        flagsLeftLbl.setText(bombs + " flags left");
    }


    void startTimer() {
        Runnable timerTask = () -> {
            if (!timerPaused) {
                gameDuration += 1;

                // Update the UI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    timeElapsedLbl.setText(gameDuration + " seconds");
                });
            }
        };

        // The timer task gets added to a new thread, and gets executed every second
        taskScheduler.scheduleAtFixedRate(timerTask, 0, 1, TimeUnit.SECONDS);
    }


    void endTimer() {
        if (taskScheduler != null) {
            taskScheduler.shutdownNow();
        }
    }


    void gameLost() {
        AudioClip clip = new AudioClip(Objects.requireNonNull(PlayController.class.getResource("/com/example/bombland/Sounds/game_lost.wav")).toExternalForm());
        clip.play();

        ArrayList<Pair<Integer, Integer>> bombCoordinates = GameMap.getInstance().getBombCoordinates();

        // Uncover all bomb tiles
        for (int i = 0; i < bombCoordinates.size(); i++) {
            Pair<Integer, Integer> coords = bombCoordinates.get(i);
            Tile tile = GameMap.getInstance().getGridObjects().get(new Pair<>(coords.getKey(), coords.getValue()));
            GameMap.getInstance().uncoverTile(tile);
            GameMap.getInstance().incrementTilesUncovered();
        }

        stackpane_child1.setEffect(new GaussianBlur()); // blurs gameplay page
        stackpane_child1.setMouseTransparent(true); // makes items in gameplay page "unclickable"

        displayGameLostPopup();
    }


    void displayGameLostPopup() {
        gameLostPopup.setManaged(true);
        gameLostPopup.setVisible(true);

        gameLostPopup.setMaxWidth(Main.mainStage.widthProperty().get() * 0.5);
        gameLostPopup.setMaxHeight(Main.mainStage.heightProperty().get() * 0.5);
        gameLostPopup.setStyle("-fx-background-radius: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        gameLostPopup_title.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        gameLostPopup_imgContainer.setStyle("-fx-pref-height: " + (Main.mainStage.getHeight() * 0.1) + "px; -fx-padding: " + (Main.mainStage.getHeight() * 0.04) + " 0 0 0;");
        gameLostPopup_img.setFitWidth(Main.mainStage.getWidth() * 0.15);
        gameLostPopup_img.setFitHeight(Main.mainStage.getWidth() * 0.15);

        gameLostPopup_timeTaken.setText(gameDuration + " second" + ((gameDuration > 1) ? "s" : ""));
        gameLostPopup_timeTaken.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.025) + "px;");

        VBox.setVgrow(gameLostPopup_buttonsContainer, Priority.ALWAYS);

        gameLostPopup_buttonsContainer.setSpacing(Main.mainStage.getWidth() * 0.05);

        gameLostPopup_playAgainBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
        gameLostPopup_mainMenuBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
    }


    void gameWon() {
        AudioClip clip = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bombland/Sounds/game_won.wav")).toExternalForm());
        clip.play();

        stackpane_child1.setEffect(new GaussianBlur()); // blurs gameplay page
        stackpane_child1.setMouseTransparent(true); // makes items in gameplay page "unclickable"

        ArrayList<JSONObject> highScores = APP_CACHE.getHighScores(GameMap.getInstance().getGameDifficulty());

        if (highScores.size() < 10 || gameDuration < highScores.get(highScores.size() - 1).getLong("score"))
            displayRecordSetPopup();
         else
             displayGameWonPopup();
    }


    void displayRecordSetPopup() {
        newRecordPopup.setManaged(true);
        newRecordPopup.setVisible(true);

        newRecordPopup.setMaxWidth(Main.mainStage.widthProperty().get() * 0.5);
        newRecordPopup.setMaxHeight(Main.mainStage.heightProperty().get() * 0.5);
        newRecordPopup.setStyle("-fx-background-radius: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        newRecordPopup_title.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        newRecordPopup_imgContainer.setStyle("-fx-pref-height: " + (Main.mainStage.getHeight() * 0.1) + "px;");
        newRecordPopup_img.setFitWidth(Main.mainStage.getWidth() * 0.15);
        newRecordPopup_img.setFitHeight(Main.mainStage.getWidth() * 0.15);

        newRecordPopup_timeTaken.setText(gameDuration + " second" + ((gameDuration > 1) ? "s" : ""));
        newRecordPopup_timeTaken.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.025) + "px;");

        newRecordPopup_text.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.02) + "px;");

        playerName_textField.setStyle("-fx-pref-width: " + (Main.mainStage.getWidth() * 0.3) + "px; -fx-pref-height: " + (Main.mainStage.getWidth() * 0.03) + "px; -fx-font-size: " + (Main.mainStage.getWidth() * 0.015) + "px;");

        VBox.setVgrow(newRecordPopup_buttonsContainer, Priority.ALWAYS);

        newRecordPopup_playAgainBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
    }


    @FXML
    void saveNewRecord() {
        if (playerName_textField.getText().isBlank()) {
            playerName_error.setVisible(true); // display error
            playerName_textField.setText("");
        }
        else {
            playerName_error.setVisible(false);
            newRecordPopup.setManaged(false);
            newRecordPopup.setVisible(false);
            displayGameWonPopup();

            Task<Void> saveHighScoreTask = new Task<>() {
                @Override
                protected Void call() {
                    JSONObject newScoreInfo = new JSONObject();
                    newScoreInfo.put("time", System.currentTimeMillis());
                    newScoreInfo.put("id", UUID.randomUUID().toString());
                    newScoreInfo.put("score", gameDuration);
                    newScoreInfo.put("name", playerName_textField.getText().strip());
                    newScoreInfo.put("difficulty", GameMap.getInstance().getGameDifficulty());
                    newScoreInfo.put("map", APP_CACHE.getGameMap());

                    DynamoDBClientUtil.saveNewHighScore(newScoreInfo, "BOMBLAND_" + GameMap.getInstance().getGameDifficulty() + "HighScores");

                    // Send new score to WebSocket server (to be distributed to other active users)
                    Main.socketClient.sendHighScore(String.valueOf(newScoreInfo));

                    playerName_textField.setText("");

                    updateAppCache(newScoreInfo);

                    return null;
                }
            };

            new Thread(saveHighScoreTask).start();
        }
    }


    static void updateAppCache(JSONObject newScoreInfo) {
        System.out.println("updateAppCache()");

        // 1. Add newScoreInfo to highScores list
        ArrayList<JSONObject> highScores = APP_CACHE.getHighScores(newScoreInfo.getString("difficulty"));
        highScores.add(newScoreInfo);
        System.out.println(highScores);

        // 2. Sort highScores list
        highScores.sort(Comparator.comparingLong(a -> a.getLong("time")));
        highScores.sort(Comparator.comparingLong(a -> a.getLong("score")));

        // 3. If highScores.size() > 10, delete last item in highScores
        if (highScores.size() > 10) {
            highScores.remove(highScores.size() - 1);
        }
    }


    void displayGameWonPopup() {
        gameWonPopup.setManaged(true);
        gameWonPopup.setVisible(true);

        gameWonPopup.setMaxWidth(Main.mainStage.widthProperty().get() * 0.5);
        gameWonPopup.setMaxHeight(Main.mainStage.heightProperty().get() * 0.5);
        gameWonPopup.setStyle("-fx-background-radius: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        gameWonPopup_title.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.04) + "px;");

        gameWonPopup_imgContainer.setStyle("-fx-pref-height: " + (Main.mainStage.getHeight() * 0.1) + "px; -fx-padding: " + (Main.mainStage.getHeight() * 0.04) + " 0 0 0;");
        gameWonPopup_img.setFitWidth(Main.mainStage.getWidth() * 0.15);
        gameWonPopup_img.setFitHeight(Main.mainStage.getWidth() * 0.15);

        gameWonPopup_timeTaken.setText(gameDuration + " second" + ((gameDuration > 1) ? "s" : ""));
        gameWonPopup_timeTaken.setStyle("-fx-font-size: " + (Main.mainStage.getWidth() * 0.025) + "px;");

        VBox.setVgrow(gameWonPopup_buttonsContainer, Priority.ALWAYS);

        gameWonPopup_buttonsContainer.setSpacing(Main.mainStage.getWidth() * 0.05);

        gameWonPopup_playAgainBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
        gameWonPopup_mainMenuBtn.setStyle("-fx-font-size: " + Main.mainStage.getWidth() * 0.015 + "px; -fx-background-radius: " + Main.mainStage.getWidth() * 0.05 + "px; -fx-pref-width: " + (Main.mainStage.getWidth() * 0.15) + "px;");
    }


    @FXML
    void playAgain() {
        if (gameLost) {
            gameLostPopup.setManaged(false);
            gameLostPopup.setVisible(false);
        }
        else {
            gameWonPopup.setManaged(false);
            gameWonPopup.setVisible(false);
        }

        stackpane_child1.setEffect(null);
        stackpane_child1.setMouseTransparent(false); // makes items in gameplay page "clickable"

        try {
            clearGrid();
            buildGrid();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public void clearGrid() throws FileNotFoundException {
        GameMap.getInstance().clearGrid();
        gridContainer.getChildren().remove(0);
    }
}