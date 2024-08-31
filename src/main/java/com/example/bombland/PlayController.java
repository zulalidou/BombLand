package com.example.bombland;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class PlayController {
    static boolean gameStarted;
    static int bombs;
    static GridPane grid;
    static HashMap<Pair<Integer, Integer>, Tile> gridObjects;

    static int rows;
    static int cols;
    static HashMap<Integer, HashSet<Integer>> tilesEliminated;
    static ArrayList<Pair<Integer, Integer>> bombCoordinates;

    @FXML
    VBox pageContainer;

    @FXML
    Label totalBombsLbl, timeElapsedLbl, flagsLeftLbl;

    @FXML
    HBox gridContainer;

    static int tilesUncovered;

    static long startTime;
    static long gameDuration;

    static int flagsSet;

    private static ScheduledExecutorService  taskScheduler;

    @FXML
    Label abc;

    static boolean gameLost;

    @FXML
    VBox stackpane_child1, gameLostPopup;

    @FXML
    Label gameLostPopup_title, gameLostPopup_timeTaken;

    @FXML
    HBox gameLostPopup_buttonsContainer;

    public PlayController() {
        gameStarted = false;
        bombs = 10;
        grid = new GridPane();
        gridObjects = new HashMap<>();
        rows = 8;
        cols = 10;
        tilesEliminated = new HashMap<>();
        bombCoordinates = new ArrayList<>();
        tilesUncovered = 0;
        flagsSet = 0;

        startTime = 0;
        gameDuration = 0;
        taskScheduler = Executors.newScheduledThreadPool(1);
        gameLost = false;
    }


    @FXML
    private void goToMainMenu() throws IOException {
        endTimer();

        ScreenController screenController = new ScreenController(pageContainer.getScene());
        screenController.removeScreen("play");

        screenController.addScreen("main", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/main-view.fxml")));
        screenController.activate("main");
    }

    @FXML
    public void initialize() throws FileNotFoundException {
        boolean evenTile = true;


        // Creates a grid of 8 rows and 10 columns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button tileBtn = new Button();
                tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + (evenTile ? "lightgreen.png" : "darkgreen.png") + "\");");
                tileBtn.setPrefHeight(100);
                tileBtn.setPrefWidth(80);

                int finalRow = row;
                int finalCol = col;
                tileBtn.setOnAction(actionEvent -> {
                    Tile tileObj = gridObjects.get(new Pair<Integer, Integer>(finalRow, finalCol));

                    if (!tileObj.isFlagged) {
                        if (!gameStarted) {
                            startTime = System.currentTimeMillis();
                            startTimer();

                            System.out.println("FIRST CLICK REGISTERED");
                            gameStarted = true;

        //                    value = TileValue.EMPTY;

                            tileObj.value = Tile.TileValue.EMPTY;
                            gridObjects.put(new Pair<Integer, Integer>(finalRow, finalCol), tileObj);

                            setupGrid(tileObj);
                        }

                        try {
                            handleTileClick(tileObj);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                tileBtn.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        System.out.println("RIGHT CLICK DETECTED!!");
                        System.out.println("row = " + finalRow + ", col = " + finalCol);

                        Tile tileObj = gridObjects.get(new Pair<Integer, Integer>(finalRow, finalCol));

                        if (tileObj.isCovered) {
                            if (tileObj.isFlagged) {
                                tileObj.isFlagged = false;
                                tileObj.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tileObj.backgroundFile + "\");");
                                flagsSet -= 1;
                            }
                            else {
                                tileObj.isFlagged = true;
                                tileObj.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tileObj.backgroundFile + "\"), url(\"/com/example/bombland/images/red-flag.png\"); -fx-background-size: 150%, 60%; -fx-background-repeat: no-repeat, no-repeat;");
                                flagsSet += 1;
                            }

                            flagsLeftLbl.setText((bombs - flagsSet) + " flags left");
                        }
                    }
                });



                Tile tileObj = new Tile(tileBtn);
//                tileObj.imageView = tileSquare;
                tileObj.row = row;
                tileObj.col = col;
                tileObj.backgroundFile = (evenTile ? "lightgreen.png" : "darkgreen.png");

                grid.add(tileBtn, col, row);
                gridObjects.put(new Pair<>(row, col), tileObj);

                evenTile = !evenTile;
            }

            evenTile = !evenTile;
        }


        /*
        grid.getColumnConstraints().add(new ColumnConstraints((50)));
        grid.getColumnConstraints().add(new ColumnConstraints((50)));

        for (int j = 0; j < 4; j++) {
            grid.getColumnConstraints().add(new ColumnConstraints((100)));
        }

        for (int j = 0; j < 4; j++) {
            grid.getRowConstraints().add(new RowConstraints((100)));
        }
        */


        grid.setGridLinesVisible(true);
        System.out.println("\n\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA:");
        System.out.println(grid.isGridLinesVisible());
//        pageContainer.getChildren().add(grid);
        gridContainer.getChildren().add(grid);

        HBox.setHgrow(gridContainer, Priority.ALWAYS);
//        grid.setStyle("-fx-pref-width: 500px");





        tilesUncovered = 0;
        startTime = 0;
        gameDuration = 0;
        timeElapsedLbl.setText("0 seconds");
        taskScheduler = Executors.newScheduledThreadPool(1);
        flagsSet = 0;
        flagsLeftLbl.setText("10 flags left");
        System.out.println("~~~~~tilesUncovered: " + tilesUncovered);
    }


    void startTimer() {
        Runnable timerTask = () -> {
            System.out.println("TIMER FUNC CALLED");
            gameDuration = (System.currentTimeMillis() - startTime) / 1000;

            // Update the UI on the JavaFX Application Thread
            Platform.runLater(() -> {
                timeElapsedLbl.setText(gameDuration + " seconds");
            });
        };

        // The timer task gets added to a new thread, and gets executed every second
        taskScheduler.scheduleAtFixedRate(timerTask, 0, 1, TimeUnit.SECONDS);
    }

    static void endTimer() {
        gameDuration = (System.currentTimeMillis() - startTime) / 1000;

        if (taskScheduler != null) {
            taskScheduler.shutdownNow();
            System.out.println("TASK-SCHEDULER SHUTDOWN CALLED!");
        }
    }

    void setupGrid(Tile tileObj) {
        System.out.println("======================================");
        System.out.println("setupGrid()");
        System.out.println("coord: " + tileObj.row + ", " + tileObj.col);

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
                    tilesEliminated.put(i, new HashSet<Integer>());
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
                Tile newBombTile = gridObjects.get(new Pair<Integer, Integer>(newRow, newCol));
                newBombTile.value = Tile.TileValue.BOMB;
                gridObjects.put(new Pair<Integer, Integer>(newRow, newCol), newBombTile);

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
                System.out.println("bomb " + bombsAssigned + ": " + newRow + ", " + newCol);
            }
        }
    }

    void setupNumberTiles(Tile tileObj) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridObjects.get(new Pair<Integer, Integer>(i, j)).value == Tile.TileValue.UNKNOWN) {
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

                            if (gridObjects.get(new Pair<Integer, Integer>(k, l)).value == Tile.TileValue.BOMB) {
                                surroundingBombs++;
                            }
                        }
                    }

                    // Shouldn't I save it back into the hashmap now that I modified the object?
                    gridObjects.get(new Pair<Integer, Integer>(i, j)).surroundingBombs = surroundingBombs;

                    if (surroundingBombs > 0) {
                        gridObjects.get(new Pair<Integer, Integer>(i, j)).value = Tile.TileValue.NUMBER;
                        gridObjects.get(new Pair<Integer, Integer>(i, j)).surroundingBombs = surroundingBombs;
                    }
                    else {
                        gridObjects.get(new Pair<Integer, Integer>(i, j)).value = Tile.TileValue.EMPTY;
                    }
                }
            }
        }
    }


    void setupEmptyTiles() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (gridObjects.get(new Pair<Integer, Integer>(i, j)).value == Tile.TileValue.UNKNOWN) {
                    gridObjects.get(new Pair<Integer, Integer>(i, j)).value = Tile.TileValue.EMPTY;
                }
            }
        }
    }


    void handleTileClick(Tile tileObj) throws IOException {
        if (tileObj.isCovered) {
            if (tileObj.value == Tile.TileValue.EMPTY) {
                traverse(tileObj.row, tileObj.col);

                System.out.println("\ntilesUncovered = " + tilesUncovered);
                if (tilesUncovered + bombs == rows * cols) {
                    System.out.println("\n\nGAME WON!!");
                    System.out.println("tilesUncovered: " + tilesUncovered);

                    endTimer();
                    gameWon();
                }
            }
            else if (tileObj.value == Tile.TileValue.NUMBER) {
                uncoverTile(tileObj);

                System.out.println("\ntilesUncovered = " + tilesUncovered);
                if (tilesUncovered + bombs == rows * cols) {
                    System.out.println("\n\nGAME WON!!");
                    System.out.println("tilesUncovered: " + tilesUncovered);

                    endTimer();
                    gameWon();
                }
            }
            else { // tile contains a bomb
                // END GAME
                gameLost = true;
                endTimer();
                gameLost();
            }
        }
    }



    void traverse(int row, int col) {
//        System.out.println("(" + row + ", " + col + ")" + "===============");

        if (row >= 0 && row < rows && col >= 0 && col < cols) {
            Tile currentTile = gridObjects.get(new Pair<Integer, Integer>(row, col));

            if (currentTile.isCovered && (currentTile.value == Tile.TileValue.EMPTY || currentTile.value == Tile.TileValue.NUMBER)) {
                uncoverTile(currentTile);

                if (currentTile.surroundingBombs > 0) {
//                    displayNumberIcon(currentTile);
                    return;
                }

                traverse(row-1, col);
                traverse(row+1, col);
                traverse(row, col-1);
                traverse(row, col+1);
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
            tile.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/red.png\"), url(\"/com/example/bombland/images/bomb.png\"); -fx-background-size: 150%, 80%; -fx-background-repeat: no-repeat, no-repeat;");
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

        tile.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tile.backgroundFile + "\"), url(\"/com/example/bombland/images/" + numberFile + "\"); -fx-background-size: 150%, 80%;");
    }

    void gameLost() {
        // Uncover all bomb tiles
        for (int i = 0; i < bombCoordinates.size(); i++) {
            Pair<Integer, Integer> coords = bombCoordinates.get(i);
            Tile tile = gridObjects.get(new Pair<Integer, Integer>(coords.getKey(), coords.getValue()));
            uncoverTile(tile);
            tilesUncovered++;
        }

        stackpane_child1.setEffect(new GaussianBlur()); // blurs gameplay page
        stackpane_child1.setMouseTransparent(true); // makes items in gameplay page "unclickable"

        gameLostPopup.setManaged(true);
        gameLostPopup.setVisible(true);
        gameLostPopup.setMaxWidth(250);
        gameLostPopup.setMaxHeight(250);

        gameLostPopup_title.setText(gameLost ? "GAME OVER" : "CONGRATULATIONS");
        gameLostPopup_timeTaken.setText(gameDuration + " seconds");

        VBox.setVgrow(gameLostPopup_buttonsContainer, Priority.ALWAYS);
        gameLostPopup_buttonsContainer.setSpacing(25);
    }



    @FXML
    void tryAgain() {
        gameLostPopup.setManaged(false);
        gameLostPopup.setVisible(false);
        stackpane_child1.setEffect(null);
        stackpane_child1.setMouseTransparent(false); // makes items in gameplay page "clickable"

        try {
            clearGrid();
            initialize();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public void clearGrid() throws FileNotFoundException {
        System.out.println("clearGrid() called");
        gameStarted = false;
        grid.getChildren().clear();
//        pageContainer.getChildren().remove(1);
        gridContainer.getChildren().remove(0);
        gridObjects = new HashMap<>();
        tilesEliminated = new HashMap<>();
        bombCoordinates = new ArrayList<>();
    }

    void gameWon() {
        pageContainer.setEffect(new GaussianBlur());
        Stage primaryStage = (Stage) pageContainer.getScene().getWindow();

        VBox popupModal = new VBox();
        Label titleLbl = new Label("~~~ CONGRATS! ~~~");
        ImageView timerImg = new ImageView("file:src/main/resources/com/example/bombland/Images/timer.png");
        Label timeTakenLbl = new Label(gameDuration + " seconds");
        Button newGameBtn = new Button("New Game");
        Button mainMenuBtn = new Button("Main Menu");
        popupModal.getChildren().addAll(titleLbl, timerImg, timeTakenLbl, newGameBtn, mainMenuBtn);

        Stage stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(primaryStage);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(popupModal));


        newGameBtn.setOnAction(event -> {
            System.out.println("NEW GAME called");
            stage.hide();
            pageContainer.setEffect(null);

            try {
                clearGrid();
                initialize();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        mainMenuBtn.setOnAction(event -> {
            System.out.println("MAIN MENU called");
            stage.hide();
            pageContainer.setEffect(null);

            ScreenController screenController = new ScreenController(grid.getScene());

            try {
                screenController.addScreen("main", FXMLLoader.load(getClass().getResource( "/com/example/bombland/FXML/main-view.fxml" )));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            screenController.activate("main");
        });

        stage.show();

        System.out.println("Time passed = " + gameDuration);
    }
}