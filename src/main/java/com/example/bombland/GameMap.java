package com.example.bombland;

import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.media.AudioClip;
import javafx.util.Pair;
import java.io.IOException;
import java.util.*;

public class GameMap {
    private static GameMap instance;

    int rows, cols, bombs, tilesUncovered, flagsSet, activeTiles;
    String gameMap, gameDifficulty;

    GridPane grid;
    ArrayList<Pair<Integer, Integer>> bombCoordinates;
    HashMap<Pair<Integer, Integer>, Tile> gridObjects;
    HashMap<Integer, HashSet<Integer>> tilesEliminated;


    private GameMap() {}

    public static GameMap getInstance() {
        if (instance == null) {
            instance = new GameMap();
        }

        return instance;
    }


    public int getBombs() {
        return bombs;
    }

    public String getGameDifficulty() {
        return gameDifficulty;
    }

    public ArrayList<Pair<Integer, Integer>> getBombCoordinates() {
        return bombCoordinates;
    }

    public HashMap<Pair<Integer, Integer>, Tile> getGridObjects() {
        return gridObjects;
    }

    void setMap() {
        gameMap = APP_CACHE.getGameMap();
        gameDifficulty = APP_CACHE.getGameDifficulty();

        if (Objects.equals(gameMap, "Rectangle")) {
            if (Objects.equals(gameDifficulty, "Easy")) {
                rows = 8;
                cols = 8;
                bombs = 10;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                rows = 16;
                cols = 16;
                bombs = 40;
            }
            else {
                rows = 16;
                cols = 32;
                bombs = 100;
            }
        }
        else if (Objects.equals(gameMap, "Bomb")) {
            if (Objects.equals(gameDifficulty, "Easy")) {
                rows = 16;
                cols = 16;
                bombs = 20;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                rows = 24;
                cols = 32;
                bombs = 45;
            }
            else {
                rows = 24;
                cols = 64;
                bombs = 200;
            }
        }
        else if (Objects.equals(gameMap, "Face")) {
            if (Objects.equals(gameDifficulty, "Easy")) {
                rows = 16;
                cols = 16;
                bombs = 25;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                rows = 24;
                cols = 32;
                bombs = 50;
            }
            else {
                rows = 24;
                cols = 64;
                bombs = 200;
            }
        }
        else { // gameMap == Flower
            if (Objects.equals(gameDifficulty, "Easy")) {
                rows = 16;
                cols = 16;
                bombs = 25;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                rows = 24;
                cols = 32;
                bombs = 60;
            }
            else {
                rows = 24;
                cols = 64;
                bombs = 150;
            }
        }
    }

    void buildGrid() {
        setMap();

        tilesUncovered = flagsSet = activeTiles = 0;
        grid = new GridPane();
        gridObjects = new HashMap<>();
        tilesEliminated = new HashMap<>();
        bombCoordinates = new ArrayList<>();

        if (Objects.equals(gameMap, "Rectangle")) {
            buildRectangleGrid();
        }
        else { // gameMap == Bomb, Face, or Flower
            buildOtherGrids();
        }
    }

    void buildRectangleGrid() {
        boolean evenTile = true;

        // Creates a grid of X rows and Y columns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button tileBtn = new Button();
                tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + (evenTile ? "lightorange.png" : "orange.png") + "\");");
                tileBtn.getStyleClass().add("tile-btn");
                tileBtn.setPrefHeight(Main.mainStage.getScene().getHeight() / rows);
                tileBtn.setPrefWidth(Main.mainStage.getScene().getWidth() / cols);

                int tileRow = row;
                int tileCol = col;

                // Handles left clicks on the tiles
                tileBtn.setOnAction(actionEvent -> {
                    Tile tileObj = gridObjects.get(new Pair<>(tileRow, tileCol));

                    if (!tileObj.isFlagged) {
                        boolean gameStarted = PlayController.getInstance().getGameStarted();

                        if (!gameStarted) {
                            PlayController.getInstance().startTimer();
                            PlayController.getInstance().setGameStarted(true);

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
                            AudioClip clip = new AudioClip(Objects.requireNonNull(GameMap.class.getResource("/com/example/bombland/Sounds/flag_flap.wav")).toExternalForm());
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

                            PlayController.getInstance().setFlagsLeftLbl((bombs - flagsSet) + " flags left");
                        }
                    }
                });

                Tile tileObj = new Tile(tileBtn);
                tileObj.row = row;
                tileObj.col = col;
                tileObj.backgroundFile = (evenTile ? "lightorange.png" : "orange.png");

                grid.add(tileBtn, col, row);
                gridObjects.put(new Pair<>(row, col), tileObj);

                evenTile = !evenTile;
                activeTiles++;
            }

            evenTile = !evenTile;
        }

        PlayController.getInstance().setGridContainer(grid);
        grid.setGridLinesVisible(true);
    }

    void buildOtherGrids() {
        // Creates a grid of X rows and Y columns
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Button tileBtn = new Button();
                tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/orange.png\");");
                tileBtn.getStyleClass().add("tile-btn");
                tileBtn.setPrefHeight(Main.mainStage.getScene().getHeight() / rows);
                tileBtn.setPrefWidth(Main.mainStage.getScene().getWidth() / cols);

                int tileRow = row;
                int tileCol = col;

                // Handles left clicks on the tiles
                tileBtn.setOnAction(actionEvent -> {
                    Tile tileObj = gridObjects.get(new Pair<>(tileRow, tileCol));

                    if (!tileObj.isFlagged) {
                        boolean gameStarted = PlayController.getInstance().getGameStarted();

                        if (!gameStarted) {
                            PlayController.getInstance().startTimer();
                            PlayController.getInstance().setGameStarted(true);

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
                            AudioClip clip = new AudioClip(Objects.requireNonNull(GameMap.class.getResource("/com/example/bombland/Sounds/flag_flap.wav")).toExternalForm());
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

                            PlayController.getInstance().setFlagsLeftLbl((bombs - flagsSet) + " flags left");
                        }
                    }
                });


                Tile tileObj = new Tile(tileBtn);
                tileObj.row = row;
                tileObj.col = col;
                tileObj.backgroundFile = "orange.png";

                if (disableTile(row, col)) {
                    tileBtn.setDisable(true);
                    tileBtn.setStyle("-fx-background-image: url(\"\")");
                    tileObj.value = Tile.TileValue.DISABLED;

                    HashSet values = null;
                    if (tilesEliminated.get(row) == null) {
                        values = new HashSet<Integer>();
                    }
                    else {
                        values = tilesEliminated.get(row);
                    }

                    values.add(col);
                    tilesEliminated.put(row, values);
                }
                else {
                    activeTiles++;
                }

                grid.add(tileBtn, col, row);
                gridObjects.put(new Pair<>(row, col), tileObj);
            }
        }

        PlayController.getInstance().setGridContainer(grid);
        grid.setGridLinesVisible(true);
    }

    boolean disableTile(int row, int col) {
        if (Objects.equals(gameMap, "Bomb")) {
            if (Objects.equals(gameDifficulty, "Easy")) {
                if (
                    (row == 0 && (col <= 4 || col == 6 || col == 9 || col >= 11)) ||
                    (row == 1 && (col <= 5 || col >= 10))  ||
                    (row == 2 && (col <= 6 || col >= 9)) ||
                    (row == 3 && (col <= 5 ||col >= 10)) ||
                    (row == 4 && (col <= 4 || col == 6 || col == 9 || col >= 11)) ||
                    (row == 5 && (col <= 3 || col == 5 || col == 6 || col == 9 || col == 10 || col >= 12)) ||
                    (row == 6 && (col <= 2 || (col >= 4 && col <= 6) || (col >= 9 && col <= 11) || col >= 13)) ||
                    (row == 7 && (col <= 4 || col >= 11)) ||
                    (row == 8 && (col <= 3 || col >= 12)) ||
                    (row == 9 && (col <= 2 || col >= 13)) ||
                    (row == 10 && (col <= 1 || col >= 14)) ||
                    (row == 11 && (col == 0 || col == 15)) ||
                    (row == 12 && (col == 0 || col == 15)) ||
                    (row == 13 && (col == 0 || col == 15)) ||
                    (row == 14 && (col <= 1 || col >= 14)) ||
                    (row == 15 && (col <= 2 || col >= 13))
                ) {
                    return true;
                }

                return false;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                if (
                    (row == 0 && (col <= 9 || (col >= 12 && col <= 14) || (col >= 17 && col <= 19) || col >= 22)) ||
                    (row == 1 && (col <= 10 || (col >= 13 && col <= 14) || (col >= 17 && col <= 18) || col >= 21)) ||
                    (row == 2 && (col <= 11 || col == 14 || col == 17 || col >= 20)) ||
                    (row == 3 && (col <= 12 || col >= 19)) ||
                    (row == 4 && (col <= 9 || col >= 22)) ||
                    (row == 5 && (col <= 9 || col >= 22)) ||
                    (row == 6 && (col <= 12 || col >= 19)) ||
                    (row == 7 && (col <= 11 || col == 14 || col == 17 || col >= 20)) ||
                    (row == 8 && (col <= 10 || (col >= 13 && col <= 14) || (col >= 17 && col <= 18) || col >= 21)) ||
                    (row == 9 && (col <= 9 || (col >= 12 && col <= 14) || (col >= 17 && col <= 19) || col >= 22)) ||
                    (row == 10 && (col <= 12 || col >= 19)) ||
                    (row == 11 && (col <= 11 || col >= 20)) ||
                    (row == 12 && (col <= 9 || col >= 22)) ||
                    (row == 13 && (col <= 8 || col >= 23)) ||
                    (row == 14 && (col <= 7 || col >= 24)) ||
                    (row == 15 && (col <= 6 || col >= 25)) ||
                    (row == 16 && (col <= 6 || col >= 25)) ||
                    (row == 17 && (col <= 6 || col >= 25)) ||
                    (row == 18 && (col <= 6 || col >= 25)) ||
                    (row == 19 && (col <= 6 || col >= 25)) ||
                    (row == 20 && (col <= 7 || col >= 24)) ||
                    (row == 21 && (col <= 8 || col >= 23)) ||
                    (row == 22 && (col <= 9 || col >= 22)) ||
                    (row == 23 && (col <= 10 || col >= 21))
                ) {
                    return true;
                }

                return false;
            }
            else { // gameDifficulty is hard
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 3 || (col >= 14 && col <= 19) || (col >= 23 && col <= 29) || (col >= 34 && col <= 40) || (col >= 44 && col <= 49) || col >= 60)) ||
                    (row == 2 && (col <= 2 || (col >= 15 && col <= 20) || (col >= 24 && col <= 29) || (col >= 34 && col <= 39) || (col >= 43 && col <= 48) || col >= 61)) ||
                    (row == 3 && (col <= 1 || (col >= 16 && col <= 21) || (col >= 25 && col <= 29) || (col >= 34 && col <= 38) || (col >= 42 && col <= 47) || col >= 62)) ||
                    (row == 4 && (col == 0 || (col >= 17 && col <= 22) || (col >= 26 && col <= 29) || (col >= 34 && col <= 37) || (col >= 41 && col <= 46) || col == 63)) ||
                    (row == 5 && ((col >= 18 && col <= 23) || (col >= 27 && col <= 29) || (col >= 34 && col <= 36) || (col >= 40 && col <= 45))) ||
                    (row == 6 && ((col >= 18 && col <= 24) || (col >= 28 && col <= 29) || (col >= 34 && col <= 35) || (col >= 39 && col <= 45))) ||
                    (row == 7 && ((col >= 20 && col <= 25) || col == 29 || col == 34 || (col >= 38 && col <= 43))) ||
                    (row == 8 && ((col >= 20 && col <= 26) || (col >= 37 && col <= 43))) ||
                    (row == 9 && ((col >= 20 && col <= 27) || (col >= 36 && col <= 43))) ||
                    (row == 14 && ((col >= 20 && col <= 27) || (col >= 36 && col <= 43))) ||
                    (row == 15 && ((col >= 20 && col <= 26) || (col >= 37 && col <= 43))) ||
                    (row == 16 && ((col >= 20 && col <= 25) || col == 29 || col == 34 || (col >= 38 && col <= 43))) ||
                    (row == 17 && ((col >= 18 && col <= 24) || (col >= 28 && col <= 29) || (col >= 34 && col <= 35) || (col >= 39 && col <= 45))) ||
                    (row == 18 && ((col >= 18 && col <= 23) || (col >= 27 && col <= 29) || (col >= 34 && col <= 36) || (col >= 40 && col <= 45))) ||
                    (row == 19 && (col == 0 || (col >= 17 && col <= 22) || (col >= 26 && col <= 29) || (col >= 34 && col <= 37) || (col >= 41 && col <= 46) || col == 63)) ||
                    (row == 20 && (col <= 1 || (col >= 16 && col <= 21) || (col >= 25 && col <= 29) || (col >= 34 && col <= 38) || (col >= 42 && col <= 47) || col >= 62)) ||
                    (row == 21 && (col <= 2 || (col >= 15 && col <= 20) || (col >= 24 && col <= 29) || (col >= 34 && col <= 39) || (col >= 43 && col <= 48) || col >= 61)) ||
                    (row == 22 && (col <= 3 || (col >= 14 && col <= 19) || (col >= 23 && col <= 29) || (col >= 34 && col <= 40) || (col >= 44 && col <= 49) || col >= 60)) ||
                    (row == 23)
                ) {
                    return true;
                }

                return false;
            }
        }
        else if (Objects.equals(gameMap, "Face")) {
            if (Objects.equals(gameDifficulty, "Easy")) {
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 4 || col >= 11))  ||
                    (row == 2 && (col <= 3 || col >= 12)) ||
                    (row == 3 && (col <= 2 ||col >= 13)) ||
                    (row == 4 && (col <= 1 || col >= 14)) ||
                    (row == 5 && (col <= 1 || col == 5 || col == 10 || col >= 14)) ||
                    (row == 6 && (col == 0 || col == 15)) ||
                    (row == 7 && (col == 0 || col == 15)) ||
                    (row == 8 && (col == 0 || col == 15)) ||
                    (row == 9 && (col == 0 || col == 4 || col == 11 || col == 15)) ||
                    (row == 10 && (col <= 1 || col == 5 || col == 10 || col >= 14)) ||
                    (row == 11 && (col <= 1 || (col >= 6 && col <= 9) || col >= 14)) ||
                    (row == 12 && (col <= 2 ||col >= 13)) ||
                    (row == 13 && (col <= 3 ||col >= 12)) ||
                    (row == 14 && (col <= 4 ||col >= 11)) ||
                    (row == 15)
                ) {
                    return true;
                }

                return false;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 11 || col >= 20)) ||
                    (row == 2 && (col <= 10 || col >= 21)) ||
                    (row == 3 && (col <= 9 || col >= 22)) ||
                    (row == 4 && (col <= 8 || col >= 23)) ||
                    (row == 5 && (col <= 7 || col >= 24)) ||
                    (row == 6 && (col <= 6 || col == 11 || col == 12 || col == 19 || col == 20 || col >= 25)) ||
                    (row == 7 && (col <= 5 || col == 11 || col == 12 || col == 19 || col == 20 || col >= 26)) ||
                    (row == 8 && (col <= 5 ||  col >= 26)) ||
                    (row == 9 && (col <= 5 || col >= 26)) ||
                    (row == 10 && (col <= 5 || col >= 26)) ||
                    (row == 11 && (col <= 5 || col >= 26)) ||
                    (row == 12 && (col <= 5 || col >= 26)) ||
                    (row == 13 && (col <= 5 || col >= 26)) ||
                    (row == 14 && (col <= 5 || col >= 26)) ||
                    (row == 15 && (col <= 5 || col >= 26)) ||
                    (row == 16 && (col <= 5 || (col >= 11 && col <= 20) || col >= 26)) ||
                    (row == 17 && (col <= 6 || (col >= 11 && col <= 20) || col >= 25)) ||
                    (row == 18 && (col <= 7 || col >= 24)) ||
                    (row == 19 && (col <= 8 || col >= 23)) ||
                    (row == 20 && (col <= 9 || col >= 22)) ||
                    (row == 21 && (col <= 10 || col >= 21)) ||
                    (row == 22 && (col <= 11 || col >= 20)) ||
                    (row == 23)
                ) {
                    return true;
                }

                return false;
            }
            else {
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 7 || (col >= 16 && col <= 27) || (col >= 36 && col <= 47) || col >= 56)) ||
                    (row == 2 && (col <= 6 || (col >= 17 && col <= 26) || (col >= 37 && col <= 46) || col >= 57)) ||
                    (row == 3 && (col <= 5 || (col >= 18 && col <= 25) || (col >= 38 && col <= 45) || col >= 58)) ||
                    (row == 4 && (col <= 4 || (col >= 19 && col <= 24) || (col >= 39 && col <= 44) || col >= 59)) ||
                    (row == 5 && (col <= 3 || (col >= 20 && col <= 23) || (col >= 40 && col <= 43) || col >= 60)) ||
                    (row == 6 && (col <= 2 || col == 6 || col == 7 || col == 16 || col == 17 || col == 21 || col == 22 || col == 27 || col == 28 || col == 35 || col == 36 || col == 41 || col == 42 || col == 47 || col == 48 || col == 55 || col == 56 || col >= 61)) ||
                    (row == 7 && (col <= 1 || col == 6 || col == 7 || col == 16 || col == 17 || col == 27 || col == 28 || col == 35 || col == 36 || col == 47 || col == 48 || col == 55 || col == 56 || col >= 61)) ||
                    (row == 8 && (col <= 1 || col >= 62)) ||
                    (row == 9 && (col <= 1 || col >= 62)) ||
                    (row == 10 && (col <= 1 || col >= 62)) ||
                    (row == 11 && (col <= 1 || col >= 62)) ||
                    (row == 12 && (col <= 1 || col >= 62)) ||
                    (row == 13 && (col <= 1 || col >= 62)) ||
                    (row == 14 && (col <= 1 || col >= 62)) ||
                    (row == 15 && (col <= 1 || col >= 62)) ||
                    (row == 16 && (col <= 1 || (col >= 9 && col <= 14) || (col >= 29 && col <= 34) || (col >= 49 && col <= 54) || col >= 62)) ||
                    (row == 17 && (col <= 2 || col == 8 || col == 15 || col == 21 || col == 22 || col == 28 || col == 35 || col == 41 || col == 42 || col == 48 || col == 55 || col >= 61)) ||
                    (row == 18 && (col <= 3 || col == 7 || col == 16 || (col >= 20 && col <= 23) || col == 27 || col == 36 || (col >= 40 && col <= 43) || col == 47 || col == 56 || col >= 60)) ||
                    (row == 19 && (col <= 4 || (col >= 19 && col <= 24) || (col >= 39 && col <= 44) || col >= 59)) ||
                    (row == 20 && (col <= 5 || (col >= 18 && col <= 25) || (col >= 38 && col <= 45) || col >= 58)) ||
                    (row == 21 && (col <= 6 || (col >= 17 && col <= 26) || (col >= 37 && col <= 46) || col >= 57)) ||
                    (row == 22 && (col <= 7 || (col >= 16 && col <= 27) || (col >= 36 && col <= 47) || col >= 56)) ||
                    (row == 23)
                ) {
                    return true;
                }

                return false;
            }
        }
        else { //gameMap == Flower
            if (Objects.equals(gameDifficulty, "Easy")) {
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 6 || col >= 9))  ||
                    (row == 2 && (col <= 5 || col >= 10)) ||
                    (row == 3 && (col <= 5 ||col >= 10)) ||
                    (row == 4 && (col <= 3 || col >= 12)) ||
                    (row == 5 && (col <= 3 || col >= 12)) ||
                    (row == 6 && (col <= 1 || col >= 14)) ||
                    (row == 7 && (col == 0 || col == 7 || col == 8 || col == 15)) ||
                    (row == 8 && (col == 0 || col == 7 || col == 8 || col == 15)) ||
                    (row == 9 && (col <= 1 || col >= 14)) ||
                    (row == 10 && (col <= 3 || col >= 12)) ||
                    (row == 11 && (col <= 3 || col >= 12)) ||
                    (row == 12 && (col <= 5 ||col >= 10)) ||
                    (row == 13 && (col <= 5 || col >= 10)) ||
                    (row == 14 && (col <= 6 || col >= 9))  ||
                    (row == 15)
                ) {
                    return true;
                }

                return false;
            }
            else if (Objects.equals(gameDifficulty, "Medium")) {
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 12 || col >= 19)) ||
                    (row == 2 && (col <= 11 || col >= 20)) ||
                    (row == 3 && (col <= 10 || col >= 21)) ||
                    (row == 4 && (col <= 10 || col >= 21)) ||
                    (row == 5 && (col <= 10 || col >= 21)) ||
                    (row == 6 && (col <= 10 || col >= 21)) ||
                    (row == 7 && (col <= 3 || col >= 28)) ||
                    (row == 8 && (col <= 2 || col >= 29)) ||
                    (row == 9 && (col <= 1 || col >= 30)) ||
                    (row == 10 && (col <= 1 || col == 15 || col == 16 || col >= 30)) ||
                    (row == 11 && (col == 0 || (col >= 14 && col <= 17) || col == 31)) ||
                    (row == 12 && (col == 0 || (col >= 14 && col <= 17) || col == 31)) ||
                    (row == 13 && (col <= 1 || col == 15 || col == 16 || col >= 30)) ||
                    (row == 14 && (col <= 1 || col >= 30)) ||
                    (row == 15 && (col <= 2 || col >= 29)) ||
                    (row == 16 && (col <= 3 || col >= 28)) ||
                    (row == 17 && (col <= 10 || col >= 21)) ||
                    (row == 18 && (col <= 10 || col >= 21)) ||
                    (row == 19 && (col <= 10 || col >= 21)) ||
                    (row == 20 && (col <= 10 || col >= 21)) ||
                    (row == 21 && (col <= 11 || col >= 20)) ||
                    (row == 22 && (col <= 12 || col >= 19)) ||
                    (row == 23)
                ) {
                    return true;
                }

                return false;
            }
            else {
                if (
                    (row == 0) ||
                    (row == 1 && (col <= 12 || (col >= 19 && col <= 44) || col >= 51)) ||
                    (row == 2 && (col <= 11 || (col >= 20 && col <= 43) || col >= 52)) ||
                    (row == 3 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 4 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 5 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 6 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 7 && (col <= 3 || (col >= 28 && col <= 35) || col >= 60)) ||
                    (row == 8 && (col <= 2 || (col >= 29 && col <= 34) || col >= 61)) ||
                    (row == 9 && (col <= 1 || (col >= 30 && col <= 33) || col >= 62)) ||
                    (row == 10 && (col <= 1 || col == 15 || col == 16 || (col >= 30 && col <= 33) || col == 47 || col == 48 || col >= 62)) ||
                    (row == 11 && (col == 0 || (col >= 14 && col <= 17) || (col >= 46 && col <= 49) || col == 63)) ||
                    (row == 12 && (col == 0 || (col >= 14 && col <= 17) || (col >= 46 && col <= 49) || col == 63)) ||
                    (row == 13 && (col <= 1 || col == 15 || col == 16 || (col >= 30 && col <= 33) || col == 47 || col == 48 || col >= 62)) ||
                    (row == 14 && (col <= 1 || (col >= 30 && col <= 33) || col >= 62)) ||
                    (row == 15 && (col <= 2 || (col >= 29 && col <= 34) || col >= 61)) ||
                    (row == 16 && (col <= 3 || (col >= 28 && col <= 35) || col >= 60)) ||
                    (row == 17 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 18 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 19 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 20 && (col <= 10 || (col >= 21 && col <= 42) || col >= 53)) ||
                    (row == 21 && (col <= 11 || (col >= 20 && col <= 43) || col >= 52)) ||
                    (row == 22 && (col <= 12 || (col >= 19 && col <= 44) || col >= 51)) ||
                    (row == 23)
                ) {
                    return true;
                }

                return false;
            }
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
            AudioClip clip = new AudioClip(Objects.requireNonNull(GameMap.class.getResource("/com/example/bombland/Sounds/tile_click.wav")).toExternalForm());
            clip.play();

            if (tileObj.value == Tile.TileValue.EMPTY) {
                traverse(tileObj.row, tileObj.col);

                if (tilesUncovered + bombs == activeTiles) {
                    PlayController.getInstance().setGameLost(false);
                    PlayController.getInstance().endTimer();
                    PlayController.getInstance().gameWon();
                }
            }
            else if (tileObj.value == Tile.TileValue.NUMBER) {
                uncoverTile(tileObj);

                if (tilesUncovered + bombs == activeTiles) {
                    PlayController.getInstance().setGameLost(false);
                    PlayController.getInstance().endTimer();
                    PlayController.getInstance().gameWon();
                }
            }
            else { // tile contains a bomb
                PlayController.getInstance().setGameLost(true);
                PlayController.getInstance().endTimer();
                PlayController.getInstance().gameLost();
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
        if (tile.isFlagged && !PlayController.getInstance().getGameLost()) {
            flagsSet--;
            PlayController.getInstance().setFlagsLeftLbl((bombs - flagsSet) + " flags left");
        }

        if (tile.value == Tile.TileValue.EMPTY) {
            tile.backgroundFile = ((tile.backgroundFile == "orange.png") ? "black.png" : "gray.png");
            tile.tileBtn.setStyle("-fx-background-image: url(\"/com/example/bombland/images/" + tile.backgroundFile + "\");");
        }
        else if (tile.value == Tile.TileValue.NUMBER) {
            tile.backgroundFile = ((tile.backgroundFile == "orange.png") ? "black.png" : "gray.png");
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

    public void incrementTilesUncovered() {
        tilesUncovered++;
    }

    public void clearGrid() {
        grid.getChildren().clear();
    }
}