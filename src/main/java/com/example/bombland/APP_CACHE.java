package com.example.bombland;

import org.json.JSONObject;
import java.util.ArrayList;

public class APP_CACHE {
    private static String identityPoolID;
    private static ArrayList<JSONObject> EASY_HIGH_SCORES;
    private static ArrayList<JSONObject> MEDIUM_HIGH_SCORES;
    private static ArrayList<JSONObject> HARD_HIGH_SCORES;

    static String getIdentityPoolID() {
        return identityPoolID;
    }

    static void setIdentityPoolID(String id) {
        identityPoolID = id;
    }

    static ArrayList<JSONObject> getHighScores(String gameMode) {
        if (gameMode.equals("Easy")) {
            return EASY_HIGH_SCORES;
        }
        else if (gameMode.equals("Medium")) {
            return MEDIUM_HIGH_SCORES;
        }
        else {
            return HARD_HIGH_SCORES;
        }
    }

    static void setHighScore(ArrayList<JSONObject> highScores, String gameMode) {
        if (gameMode.equals("Easy")) {
            EASY_HIGH_SCORES = highScores;
        }
        else if (gameMode.equals("Medium")) {
            MEDIUM_HIGH_SCORES = highScores;
        }
        else {
            HARD_HIGH_SCORES = highScores;
        }
    }
}