package com.example.bombland;

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
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.net.http.HttpClient;

public class Main extends Application {
    static Stage mainStage = null;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;

        fetchData();

        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bombland/Images/bombsmall.png")));
        stage.getIcons().add(icon);
        stage.setTitle("BOMBLAND");
        stage.setResizable(false);

        showSplashScreen(stage);

        showMainMenu(mainStage);
    }

    @Override
    public void stop() {
        // Closing the entire app doesn't kill all threads, so I'm explicitly having the executor service kill all tasks I queued in the background
        PlayController.endTimer();
    }

    public static void main(String[] args) {
        launch();
    }

    public void fetchData() {
        Task<Void> fetchDataTask = new Task<>() {
            @Override
            protected Void call() {
                getEnvironmentVariables();
                DynamoDBClientUtil.getHighScores();

                return null;
            }
        };

        new Thread(fetchDataTask).start();
    }

    public void getEnvironmentVariables() {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bombland-server.onrender.com/get-environment-variables"))
                .GET()
                .build();

        try {
            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject environmentVarsObj = new JSONObject(response.body());
            APP_CACHE.setIdentityPoolID(environmentVarsObj.getString("identityPoolID"));
        } catch (Exception e) {
            System.out.println("\n\nUH OH. AN ERROR OCCURRED!");
            e.printStackTrace();
        }
    }

    public void showSplashScreen(Stage stage) {
        Text textBeforeO = new Text("B");
        textBeforeO.styleProperty().bind(
                // Sets the font size to be 9% of the app window's width
                Bindings.format("-fx-font-size: %.2fpx; -fx-font-weight: bold;", stage.widthProperty().multiply(0.09))
        );

        Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bombland/Images/bomb.png")));
        ImageView imageView = new ImageView(image);

        Text textAfterO = new Text("MBLAND");
        textAfterO.styleProperty().bind(
                // Sets the font size to be 9% of the app window's width
                Bindings.format("-fx-font-size: %.2fpx; -fx-font-weight: bold;", stage.widthProperty().multiply(0.09))
        );


        HBox logoContainer = new HBox(textBeforeO, imageView, textAfterO);
        logoContainer.setAlignment(javafx.geometry.Pos.CENTER);

        VBox splashScreen = new VBox(logoContainer);
        splashScreen.setAlignment(javafx.geometry.Pos.CENTER);

        Scene splashScene = new Scene(splashScreen, 1024, 768);
        stage.setScene(splashScene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            int i = 0;

            @Override
            public void handle(long now) {
                splashScreen.setStyle("-fx-background-color: rgb(" + i + ", 0, 0);");
                i++;

                if (i >= 255) {
                    stop(); // Stop the AnimationTimer

                    try {
                        showMainMenu(stage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        timer.start();
    }

    public void showMainMenu(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("FXML/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        stage.setScene(scene);
        stage.show();
    }
}
