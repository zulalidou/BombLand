package com.example.bombland;

import java.util.TreeMap;

// 1. The keys in the maps are the players' scores
// 2. The values in the maps are the players' names
// 3. TreeMaps are used because they automatically sort their entries based on the keys
public class APP_CACHE {
    private static final TreeMap<Integer, String> EASY_HIGH_SCORES = new TreeMap<>();
    private static final TreeMap<Integer, String> MEDIUM_HIGH_SCORES = new TreeMap<>();
    private static final TreeMap<Integer, String> HARD_HIGH_SCORES = new TreeMap<>();


    static TreeMap<Integer, String> getEasyHighScores() {
        return EASY_HIGH_SCORES;
    }

    static void saveEasyHighScore(Integer score, String name) {
        EASY_HIGH_SCORES.put(score, name);
    }

    static TreeMap<Integer, String> getMediumHighScores() {
        return MEDIUM_HIGH_SCORES;
    }

    static void saveMediumHighScore(Integer score, String name) {
        MEDIUM_HIGH_SCORES.put(score, name);
    }

    static TreeMap<Integer, String> getHardHighScores() {
        return HARD_HIGH_SCORES;
    }

    static void saveHardHighScore(Integer score, String name) {
        HARD_HIGH_SCORES.put(score, name);
    }
}
