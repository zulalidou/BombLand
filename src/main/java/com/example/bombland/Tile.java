package com.example.bombland;

public class Tile {
    boolean isCovered;

    enum TileValue {
        UNKNOWN,
        EMPTY,
        NUMBER,
        BOMB
    }
    TileValue value;

    Tile() {
        isCovered = true;
        value = TileValue.UNKNOWN;
    }
}
