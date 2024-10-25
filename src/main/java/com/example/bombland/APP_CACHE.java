package com.example.bombland;

import org.json.JSONObject;
import java.util.ArrayList;

public class APP_CACHE {
    private static final ArrayList<JSONObject> EASY_HIGH_SCORES = new ArrayList<>();
    private static final ArrayList<JSONObject> MEDIUM_HIGH_SCORES = new ArrayList<>();
    private static final ArrayList<JSONObject> HARD_HIGH_SCORES = new ArrayList<>();


    static ArrayList<JSONObject> getEasyHighScores() {
        return EASY_HIGH_SCORES;
    }

    static ArrayList<JSONObject> getMediumHighScores() {
        return MEDIUM_HIGH_SCORES;
    }

    static ArrayList<JSONObject> getHardHighScores() {
        return HARD_HIGH_SCORES;
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
}