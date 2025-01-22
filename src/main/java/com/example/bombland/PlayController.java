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
import javafx.scene.input.MouseButton;
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
    static boolean gameStarted, gameLost;
    static int rows, cols, bombs, tilesUncovered, flagsSet;
    static String gameMode;

    static GridPane grid;
    static HashMap<Pair<Integer, Integer>, Tile> gridObjects;
    static HashMap<Integer, HashSet<Integer>> tilesEliminated;
    static ArrayList<Pair<Integer, Integer>> bombCoordinates;

    static long gameDuration;

    private static ScheduledExecutorService  taskScheduler;

    static boolean timerPaused;


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

    static void setMode(String mode) {
        if (Objects.equals(mode, "Easy")) {
            rows = 8;
            cols = 8;
            bombs = 10;
            gameMode = "Easy";
        }
        else if (Objects.equals(mode, "Medium")) {
            rows = 16;
            cols = 16;
            bombs = 40;
            gameMode = "Medium";
        }
        else {
            rows = 16;
            cols = 32;
            bombs = 100;
            gameMode = "Hard";
        }
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
        tilesUncovered = flagsSet = 0;

        grid = new GridPane();
        gridObjects = new HashMap<>();
        tilesEliminated = new HashMap<>();
        bombCoordinates = new ArrayList<>();

        gameDuration = -1;

        taskScheduler = Executors.newScheduledThreadPool(1);

        timerPaused = false;

        timeElapsedLbl.setText("0 seconds");
        totalBombsLbl.setText(bombs + " bombs");
        flagsLeftLbl.setText(bombs + " flags left");


        boolean evenTile = true;

        // Creates a grid of X rows and Y columns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button tileBtn = new Button();
                tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + (evenTile ? "lightgreen.png" : "darkgreen.png") + "\");");
                tileBtn.setPrefHeight(Main.mainStage.getScene().getHeight() / rows);
                tileBtn.setPrefWidth(Main.mainStage.getScene().getWidth() / cols);


                int tileRow = row;
                int tileCol = col;

                // Handles left clicks on the tiles
                tileBtn.setOnAction(actionEvent -> {
                    Tile tileObj = gridObjects.get(new Pair<>(tileRow, tileCol));

                    if (!tileObj.isFlagged) {
                        if (!gameStarted) {
                            startTimer();

                            gameStarted = true;

                            tileObj.value = Tile.TileValue.EMPTY;
                            gridObjects.put(new Pair<>(tileRow, tileCol), tileObj);

                            setupGrid(tileObj);
                        }

                        try {
                            handleTileClick(tileObj);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                // Handles right clicks on the tiles
                tileBtn.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        Tile tileObj = gridObjects.get(new Pair<>(tileRow, tileCol));

                        if (tileObj.isCovered) {
                            AudioClip clip = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bombland/Sounds/flag_flap.wav")).toExternalForm());
                            clip.play();

                            if (tileObj.isFlagged) {
                                tileObj.isFlagged = false;
                                tileObj.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tileObj.backgroundFile + "\");");
                                flagsSet -= 1;
                            }
                            else {
                                tileObj.isFlagged = true;
                                tileObj.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tileObj.backgroundFile + "\"), url(\"/com/example/bombland/images/red-flag.png\"); -fx-background-size: 200%, 50%;");
                                flagsSet += 1;
                            }

                            flagsLeftLbl.setText((bombs - flagsSet) + " flags left");
                        }
                    }
                });

                Tile tileObj = new Tile(tileBtn);
                tileObj.row = row;
                tileObj.col = col;
                tileObj.backgroundFile = (evenTile ? "lightgreen.png" : "darkgreen.png");

                grid.add(tileBtn, col, row);
                gridObjects.put(new Pair<>(row, col), tileObj);

                evenTile = !evenTile;
            }

            evenTile = !evenTile;
        }

        gridContainer.getChildren().add(grid);
        grid.setGridLinesVisible(true);
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


    static void endTimer() {
        if (taskScheduler != null) {
            taskScheduler.shutdownNow();
        }
    }


    void setupGrid(Tile tileObj) {
        eliminateSurroundingTiles(tileObj);
        setupBombTiles();
        setupNumberTiles(tileObj);
        setupEmptyTiles();
    }


    // This function makes sure that there are no adjacent bombs on the first tile clicked
    void eliminateSurroundingTiles(Tile tileObj) {
        int rowCount = 1;

        for (int i = tileObj.row - 1; rowCount <= 3; rowCount++, i++) {
            if (i < 0 || i > rows - 1) {
                continue;
            }

            int colCount = 1;

            for (int j = tileObj.col - 1; colCount <= 3; colCount++, j++) {
                if (j < 0 || j > cols - 1) {
                    continue;
                }

                if (tilesEliminated.get(i) == null) {
                    tilesEliminated.put(i, new HashSet<>());
                }

                HashSet<Integer> values = tilesEliminated.get(i);
                values.add(j);
                tilesEliminated.put(i, values);
            }
        }
    }


    void setupBombTiles() {
        int bombsAssigned = 0;

        while (bombsAssigned < bombs) {
            Random rand = new Random();
            int newRow = rand.nextInt(rows);
            int newCol = rand.nextInt(cols);

            // Checks if it's safe to place a bomb on the coordinates generated
            if (tilesEliminated.get(newRow) == null || !(tilesEliminated.get(newRow).contains(newCol))) {
                // Set tile value to bomb
                Tile newBombTile = gridObjects.get(new Pair<>(newRow, newCol));
                newBombTile.value = Tile.TileValue.BOMB;
                gridObjects.put(new Pair<>(newRow, newCol), newBombTile);

                HashSet values;
                if (tilesEliminated.get(newRow) == null) {
                    values = new HashSet<Integer>();
                }
                else {
                    values = tilesEliminated.get(newRow);
                }

                values.add(newCol);
                tilesEliminated.put(newRow, values);

                bombsAssigned++;
                bombCoordinates.add(new Pair<>(newRow, newCol));
            }
        }
    }


    void setupNumberTiles(Tile tileObj) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridObjects.get(new Pair<>(i, j)).value == Tile.TileValue.UNKNOWN) {
                    int kCount = 0;
                    int surroundingBombs = 0;

                    for (int k = i - 1; kCount < 3; k++, kCount++) {
                        if (k < 0 || k > rows - 1) {
                            continue;
                        }

                        int lCount = 0;

                        for (int l = j - 1; lCount < 3; l++, lCount++) {
                            if (l < 0 || l > cols - 1 || (k == tileObj.row && l == tileObj.col)) {
                                continue;
                            }

                            if (gridObjects.get(new Pair<>(k, l)).value == Tile.TileValue.BOMB) {
                                surroundingBombs++;
                            }
                        }
                    }

                    gridObjects.get(new Pair<>(i, j)).surroundingBombs = surroundingBombs;

                    if (surroundingBombs > 0) {
                        gridObjects.get(new Pair<>(i, j)).value = Tile.TileValue.NUMBER;
                        gridObjects.get(new Pair<>(i, j)).surroundingBombs = surroundingBombs;
                    }
                    else {
                        gridObjects.get(new Pair<>(i, j)).value = Tile.TileValue.EMPTY;
                    }
                }
            }
        }
    }


    void setupEmptyTiles() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridObjects.get(new Pair<>(i, j)).value == Tile.TileValue.UNKNOWN) {
                    gridObjects.get(new Pair<>(i, j)).value = Tile.TileValue.EMPTY;
                }
            }
        }
    }


    void handleTileClick(Tile tileObj) throws IOException {
        if (tileObj.isCovered) {
            AudioClip clip = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bombland/Sounds/tile_click.wav")).toExternalForm());
            clip.play();

            if (tileObj.value == Tile.TileValue.EMPTY) {
                traverse(tileObj.row, tileObj.col);

                if (tilesUncovered + bombs == rows * cols) {
                    gameLost = false;
                    endTimer();
                    gameWon();
                }
            }
            else if (tileObj.value == Tile.TileValue.NUMBER) {
                uncoverTile(tileObj);

                if (tilesUncovered + bombs == rows * cols) {
                    gameLost = false;
                    endTimer();
                    gameWon();
                }
            }
            else { // tile contains a bomb
                gameLost = true;
                endTimer();
                gameLost();
            }
        }
    }


    void traverse(int row, int col) {
        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            Tile currentTile = gridObjects.get(new Pair<>(row, col));

            if (currentTile.isCovered && (currentTile.value == Tile.TileValue.EMPTY || currentTile.value == Tile.TileValue.NUMBER)) {
                uncoverTile(currentTile);

                if (currentTile.surroundingBombs > 0) {
                    return;
                }

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        traverse(row + i, col + j);
                    }
                }
            }
        }
    }


    void uncoverTile(Tile tile) {
        if (tile.isFlagged && !gameLost) {
            flagsSet--;
            flagsLeftLbl.setText((bombs - flagsSet) + " flags left");
        }

        if (tile.value == Tile.TileValue.EMPTY) {
            tile.backgroundFile = ((tile.backgroundFile == "darkgreen.png") ? "tan.png" : "blanchedalmond.png");
            tile.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tile.backgroundFile + "\");");
        }
        else if (tile.value == Tile.TileValue.NUMBER) {
            tile.backgroundFile = ((tile.backgroundFile == "darkgreen.png") ? "tan.png" : "blanchedalmond.png");
            displayNumberIcon(tile);
        }
        else { // bomb tile
            tile.backgroundFile = "red.png";
            tile.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/red.png\"), url(\"/com/example/bombland/images/bomb.png\"); -fx-background-size: 200%, 60%;");
        }

        tile.isCovered = false;
        tilesUncovered++;
    }


    void displayNumberIcon(Tile tile) {
        StringBuilder numberFile = new StringBuilder();

        switch(tile.surroundingBombs) {
            case 1:
                numberFile = new StringBuilder("1.png");
                break;
            case 2:
                numberFile = new StringBuilder("2.png");
                break;
            case 3:
                numberFile = new StringBuilder("3.png");
                break;
            case 4:
                numberFile = new StringBuilder("4.png");
                break;
            case 5:
                numberFile = new StringBuilder("5.png");
                break;
            case 6:
                numberFile = new StringBuilder("6.png");
                break;
            case 7:
                numberFile = new StringBuilder("7.png");
                break;
            case 8:
                numberFile = new StringBuilder("8.png");
                break;
        }

        tile.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tile.backgroundFile + "\"), url(\"/com/example/bombland/images/" + numberFile + "\"); -fx-background-size: 200%, 60%;");
    }


    void gameLost() {
        AudioClip clip = new AudioClip(Objects.requireNonNull(getClass().getResource("/com/example/bombland/Sounds/game_lost.wav")).toExternalForm());
        clip.play();

        // Uncover all bomb tiles
        for (int i = 0; i < bombCoordinates.size(); i++) {
            Pair<Integer, Integer> coords = bombCoordinates.get(i);
            Tile tile = gridObjects.get(new Pair<>(coords.getKey(), coords.getValue()));
            uncoverTile(tile);
            tilesUncovered++;
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

        ArrayList<JSONObject> highScores = APP_CACHE.getHighScores(gameMode);

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

                    DynamoDBClientUtil.saveNewHighScore(newScoreInfo, "BOMBLAND_" + gameMode + "HighScores");

                    playerName_textField.setText("");


                    // 1. Add newScoreInfo to highScores list
                    ArrayList<JSONObject> highScores = APP_CACHE.getHighScores(gameMode);
                    highScores.add(newScoreInfo);

                    // 2. Send new score to WebSocket server (to be distributed to other active users)
                    Main.socketClient.sendHighScore(String.valueOf(newScoreInfo));

                    // 3. Sort highScores list
                    highScores.sort(new Comparator<JSONObject>() {
                        @Override
                        public int compare(JSONObject a, JSONObject b) {
                            return Long.compare(a.getLong("score"), b.getLong("score"));
                        }
                    });

                    // 4. If highScores.size() > 10, delete last item in highScores
                    if (highScores.size() > 10) {
                        highScores.remove(highScores.size() - 1);
                    }

                    return null;
                }
            };

            new Thread(saveHighScoreTask).start();
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
        grid.getChildren().clear();
        gridContainer.getChildren().remove(0);
    }
}