package com.example.bombland;

import org.json.JSONObject;
import java.util.ArrayList;

public class APP_CACHE {
    private static boolean gettingData = false;
    private static String identityPoolID;
    private static ArrayList<JSONObject> EASY_HIGH_SCORES;
    private static ArrayList<JSONObject> MEDIUM_HIGH_SCORES;
    private static ArrayList<JSONObject> HARD_HIGH_SCORES;
    private static String gameDifficulty;
    private static String gameMap;
    private static String mapOfHighScoresBeingShown = "";



    static boolean isGettingData() {
        return gettingData;
    }

    static void setGettingData(boolean value) {
        gettingData = value;
    }

    static String getIdentityPoolID() {
        return identityPoolID;
    }

    static void setIdentityPoolID(String id) {
        identityPoolID = id;
    }

    static String getGameDifficulty() {
        return gameDifficulty;
    }

    static void setGameDifficulty(String gameDiff) {
        gameDifficulty = gameDiff;
    }

    static String getGameMap() {
        return gameMap;
    }

    static void setGameMap(String newMap) {
        gameMap = newMap;
    }

    static String getMapOfHighScoresBeingShown() {
        return mapOfHighScoresBeingShown;
    }

    static void setMapOfHighScoresBeingShown(String newMap) {
        mapOfHighScoresBeingShown = newMap;
    }

    static ArrayList<JSONObject> getHighScores(String gameDifficulty) {
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

    static void setHighScore(ArrayList<JSONObject> highScores, String gameDifficulty) {
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