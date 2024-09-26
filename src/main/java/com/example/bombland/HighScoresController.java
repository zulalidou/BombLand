package com.example.bombland;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.IOException;
import org.bson.Document;
import org.json.JSONObject;

public class HighScoresController {
    private final MongoDBConnection mongoDBConnection = new MongoDBConnection();
    private MongoDatabase database;

    @FXML
    VBox highScoresPage, easyHighScoresContainer, mediumHighScoresContainer, hardHighScoresContainer;

    @FXML
    HBox highScoresContainer_bottom, highScoresContainer_leftChild, highScoresContainer_middleChild, highScoresContainer_rightChild;

    @FXML
    ScrollPane easyHighScores_scrollPane, mediumHighScores_scrollPane, hardHighScores_scrollPane;

    @FXML
    VBox easyHighScores_container, mediumHighScores_container, hardHighScores_container;


    @FXML
    public void initialize() {
        HBox.setHgrow(highScoresContainer_leftChild, Priority.ALWAYS);
        HBox.setHgrow(highScoresContainer_middleChild, Priority.ALWAYS);
        HBox.setHgrow(highScoresContainer_rightChild, Priority.ALWAYS);

        HBox.setHgrow(easyHighScoresContainer, Priority.ALWAYS);
        HBox.setHgrow(mediumHighScoresContainer, Priority.ALWAYS);
        HBox.setHgrow(hardHighScoresContainer, Priority.ALWAYS);


        VBox.setVgrow(highScoresContainer_bottom, Priority.ALWAYS);
        VBox.setVgrow(easyHighScores_scrollPane, Priority.ALWAYS);
        VBox.setVgrow(mediumHighScores_scrollPane, Priority.ALWAYS);
        VBox.setVgrow(hardHighScores_scrollPane, Priority.ALWAYS);

//        VBox.setVgrow(easyHighScoresContainer, Priority.ALWAYS);
//        VBox.setVgrow(mediumHighScoresContainer, Priority.ALWAYS);
//        VBox.setVgrow(hardHighScoresContainer, Priority.ALWAYS);


        displayHighScores();
    }

    private void displayHighScores() {
        mongoDBConnection.connect("mongodb+srv://bomblandAdmin:iIbydSYKZ6EVn2Cy@cluster0.ilt6y.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0", "HighScores");
        database = mongoDBConnection.getDatabase();

        MongoCollection<Document> easyTable = database.getCollection("Easy");
        MongoCollection<Document> mediumTable = database.getCollection("Medium");
        MongoCollection<Document> hardTable = database.getCollection("Hard");

        System.out.println(easyTable.countDocuments());
        System.out.println(mediumTable.countDocuments());
        System.out.println(hardTable.countDocuments());


        if (easyTable.countDocuments() > 0) {
            easyHighScores_container.getChildren().clear();

            MongoCursor<Document> cursor = easyTable.find().iterator();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());

                Label scoreBox = new Label(scoreObj.getString("name") + ", " + scoreObj.getString("score"));
                easyHighScores_container.getChildren().add(scoreBox);
            }
        }


        if (mediumTable.countDocuments() > 0) {
            mediumHighScores_container.getChildren().clear();

            MongoCursor<Document> cursor = mediumTable.find().iterator();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());

                Label scoreBox = new Label(scoreObj.getString("name") + ", " + scoreObj.getString("score"));
                mediumHighScores_container.getChildren().add(scoreBox);
            }
        }


        if (hardTable.countDocuments() > 0) {
            hardHighScores_container.getChildren().clear();

            MongoCursor<Document> cursor = hardTable.find().iterator();

            while (cursor.hasNext()) {
                JSONObject scoreObj = new JSONObject(cursor.next().toJson());

                Label scoreBox = new Label(scoreObj.getString("name") + ", " + scoreObj.getString("score"));
                hardHighScores_container.getChildren().add(scoreBox);
            }
        }


        mongoDBConnection.close();
    }

    @FXML
    private void goToMainMenu() throws IOException {
        ScreenController screenController = new ScreenController(highScoresPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/main-view.fxml")));
        screenController.activate("main");
    }
}
