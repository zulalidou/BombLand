package com.example.bombland;

import org.json.JSONObject;
import java.util.ArrayList;

public class APP_CACHE {
    private static APP_CACHE instance;
    private boolean gettingData = false;
    private String identityPoolID;
    private ArrayList<JSONObject> EASY_HIGH_SCORES;
    private ArrayList<JSONObject> MEDIUM_HIGH_SCORES;
    private ArrayList<JSONObject> HARD_HIGH_SCORES;
    private String gameDifficulty;
    private String gameMap;
    private String mapOfHighScoresBeingShown = "";


    private APP_CACHE() {}

    public static APP_CACHE getInstance() {
        if (instance == null) {
            instance = new APP_CACHE();
        }

        return instance;
    }


    boolean isGettingData() {
        return gettingData;
    }

    void setGettingData(boolean value) {
        gettingData = value;
    }

    String getIdentityPoolID() {
        return identityPoolID;
    }

    void setIdentityPoolID(String id) {
        identityPoolID = id;
    }

    String getGameDifficulty() {
        return gameDifficulty;
    }

    void setGameDifficulty(String gameDiff) {
        gameDifficulty = gameDiff;
    }

    String getGameMap() {
        return gameMap;
    }

    void setGameMap(String newMap) {
        gameMap = newMap;
    }

    String getMapOfHighScoresBeingShown() {
        return mapOfHighScoresBeingShown;
    }

    void setMapOfHighScoresBeingShown(String newMap) {
        mapOfHighScoresBeingShown = newMap;
    }

    ArrayList<JSONObject> getHighScores(String gameDifficulty) {
        if (gameDifficulty.equals("Easy")) {
            return EASY_HIGH_SCORES;
        }
        else if (gameDifficulty.equals("Medium")) {
            return MEDIUM_HIGH_SCORES;
        }
        else {
            return HARD_HIGH_SCORES;
        }
    }

    void setHighScore(ArrayList<JSONObject> highScores, String gameDifficulty) {
        if (gameDifficulty.equals("Easy")) {
            EASY_HIGH_SCORES = highScores;
        }
        else if (gameDifficulty.equals("Medium")) {
            MEDIUM_HIGH_SCORES = highScores;
        }
        else {
            HARD_HIGH_SCORES = highScores;
        }
    }
}