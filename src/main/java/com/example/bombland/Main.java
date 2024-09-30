package com.example.bombland;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.bson.Document;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("BOMBLAND");
        stage.setResizable(false);

        Image icon = new Image(getClass().getResourceAsStream("/com/example/bombland/images/bombsmall.png"));
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();


        Task<Void> fetchHighScoresTask = new Task<>() {
            @Override
            protected Void call() {
                getHighScores();
                sortHighScores(APP_CACHE.getEasyHighScores());
                sortHighScores(APP_CACHE.getMediumHighScores());
                sortHighScores(APP_CACHE.getHardHighScores());
                return null;
            }
        };

        new Thread(fetchHighScoresTask).start();
    }

    @Override
    public void stop() {
        // Closing the entire app doesn't kill all threads, so I'm explicitly having the executor service kill all tasks I queued in the background
        PlayController.endTimer();
    }

    public static void main(String[] args) {
        launch();
    }

    public void getHighScores() {
        MongoDBConnection mongoDBConnection = new MongoDBConnection();
        mongoDBConnection.connect("mongodb+srv://bomblandAdmin:iIbydSYKZ6EVn2Cy@cluster0.ilt6y.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0", "HighScores");

        MongoDatabase database = mongoDBConnection.getDatabase();
        MongoCollection<Document> easyHighScores_Table = database.getCollection("EASY");
        MongoCollection<Document> mediumHighScores_Table = database.getCollection("MEDIUM");
        MongoCollection<Document> hardHighScores_Table = database.getCollection("HARD");

        if (easyHighScores_Table.countDocuments() > 0) {
            MongoCursor<Document> cursor = easyHighScores_Table.find().iterator();
            ArrayList<JSONObject> easyHighScores_cache = APP_CACHE.getEasyHighScores();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());
                easyHighScores_cache.add(scoreObj);
            }
        }

        if (mediumHighScores_Table.countDocuments() > 0) {
            MongoCursor<Document> cursor = mediumHighScores_Table.find().iterator();
            ArrayList<JSONObject> mediumHighScores_cache = APP_CACHE.getMediumHighScores();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());
                mediumHighScores_cache.add(scoreObj);
            }
        }

        if (hardHighScores_Table.countDocuments() > 0) {
            MongoCursor<Document> cursor = hardHighScores_Table.find().iterator();
            ArrayList<JSONObject> hardHighScores_cache = APP_CACHE.getHardHighScores();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());
                hardHighScores_cache.add(scoreObj);
            }
        }

        mongoDBConnection.close();
    }

    public void sortHighScores(ArrayList<JSONObject> highScores) {
        highScores.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                return Long.compare(a.getLong("time"), b.getLong("time"));
            }
        });

        highScores.sort(new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                return Long.compare(a.getLong("score"), b.getLong("score"));
            }
        });
    }
}
