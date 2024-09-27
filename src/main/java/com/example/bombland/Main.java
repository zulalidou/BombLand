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


        Task<Void> fetchHighScoresTask = new Task<Void>() {
            @Override
            protected Void call() {
                getHighScores();
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
        MongoCollection<Document> easyHighScores = database.getCollection("Easy");
        MongoCollection<Document> mediumHighScores = database.getCollection("Medium");
        MongoCollection<Document> hardHighScores = database.getCollection("Hard");

        if (easyHighScores.countDocuments() > 0) {
            MongoCursor<Document> cursor = easyHighScores.find().iterator();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());
                APP_CACHE.saveEasyHighScore(scoreObj.getInt("score"), scoreObj.getString("name"));
            }
        }

        if (mediumHighScores.countDocuments() > 0) {
            MongoCursor<Document> cursor = mediumHighScores.find().iterator();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());
                APP_CACHE.saveMediumHighScore(scoreObj.getInt("score"), scoreObj.getString("name"));
            }
        }

        if (hardHighScores.countDocuments() > 0) {
            MongoCursor<Document> cursor = hardHighScores.find().iterator();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());
                APP_CACHE.saveHardHighScore(scoreObj.getInt("score"), scoreObj.getString("name"));
            }
        }

        mongoDBConnection.close();
    }
}
