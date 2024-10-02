package com.example.bombland;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class Main extends Application {
    static Stage mainStage = null;

    @Override
    public void start(Stage stage) {
        mainStage = stage;

        fetchHighScores();

        Image icon = new Image(getClass().getResourceAsStream("/com/example/bombland/images/bombsmall.png"));
        mainStage.getIcons().add(icon);
        mainStage.setTitle("BOMBLAND");
        mainStage.setResizable(false);

        showSplashScreen();
    }

    @Override
    public void stop() {
        // Closing the entire app doesn't kill all threads, so I'm explicitly having the executor service kill all tasks I queued in the background
        PlayController.endTimer();
    }

    public static void main(String[] args) {
        launch();
    }

    public void fetchHighScores() {
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

    public void showSplashScreen() {
        Text textBeforeO = new Text("B");
        textBeforeO.styleProperty().bind(
                // Sets the font size to be 9% of the app window's width
                Bindings.format("-fx-font-size: %.2fpx; -fx-font-weight: bold;", mainStage.widthProperty().multiply(0.09))
        );

        Image image = new Image(getClass().getResourceAsStream("/com/example/bombland/images/bomb.png"));
        ImageView imageView = new ImageView(image);

        Text textAfterO = new Text("MBLAND");
        textAfterO.styleProperty().bind(
                // Sets the font size to be 9% of the app window's width
                Bindings.format("-fx-font-size: %.2fpx; -fx-font-weight: bold;", mainStage.widthProperty().multiply(0.09))
        );


        HBox logoContainer = new HBox(textBeforeO, imageView, textAfterO);
        logoContainer.setAlignment(javafx.geometry.Pos.CENTER);

        VBox splashScreen = new VBox(logoContainer);
        splashScreen.setAlignment(javafx.geometry.Pos.CENTER);

        Scene splashScene = new Scene(splashScreen, 1024, 768);
        mainStage.setScene(splashScene);
        mainStage.show();

        AnimationTimer timer = new AnimationTimer() {
            int i = 0;

            @Override
            public void handle(long now) {
                splashScreen.setStyle("-fx-background-color: rgb(" + i + ", 0, 0);");
                i++;

                if (i >= 255) {
                    stop(); // Stop the AnimationTimer

                    try {
                        showMainMenu();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        timer.start();
    }
    
    public void showMainMenu() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        mainStage.setScene(scene);
        mainStage.show();
    }
}
