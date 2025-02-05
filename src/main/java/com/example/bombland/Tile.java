package com.example.bombland;

import javafx.scene.control.*;

public class Tile {
    boolean isCovered, isFlagged;
    TileValue value;
    String backgroundFile;
    int row, col, surroundingBombs;
    Button tileBtn;

    enum TileValue {
        UNKNOWN,
        EMPTY,
        NUMBER,
        BOMB,
        DISABLED
    }

    Tile(Button tb) {
        isCovered = true;
        isFlagged = false;
        value = TileValue.UNKNOWN;
        backgroundFile = "";
        row = col = -1;
        surroundingBombs = 0;
        tileBtn = tb;
    }
}
