package com.example.bombland;

import javafx.scene.control.*;

public class Tile {
    boolean isCovered;
    TileValue value;
//    ImageView imageView;
    String backgroundFile;
    int row;
    int col;
    int surroundingBombs;
    Button tileBtn;

    enum TileValue {
        UNKNOWN,
        EMPTY,
        NUMBER,
        BOMB
    }

    Tile(Button tb) {
        isCovered = true;
        value = TileValue.UNKNOWN;
//        imageView = img;
        backgroundFile = "";
        row = col = -1;
        surroundingBombs = 0;
        tileBtn = tb;
    }
}
