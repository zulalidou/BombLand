package com.example.bombland;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class MapSelectionController {
    @FXML
    Button backBtn;

    @FXML
    VBox mapSelectionPage, mapSelectionPageContainer, mapSelectionPageContainer_bottom, rectangleMapSelectorContainer, bombMapSelectorContainer, faceMapSelectorContainer, octagonMapSelectorContainer;

    @FXML
    HBox mapSelectionPageContainer_top, mapSelectionPageContainer_top_leftChild, mapSelectionPageContainer_top_middleChild, mapSelectionPageContainer_top_rightChild, upperMapsContainer, lowerMapsContainer;

    @FXML
    Label mapSelectionPage_title, rectangleMapSelectorName, bombMapSelectorName, faceMapSelectorName, octagonMapSelectorName;

    @FXML
    Region space1, space2;

    @FXML
    Image face_map_img;

    @FXML
    ImageView face_map_imgView;


    @FXML
    public void initialize() {
        // Prevents the width of the difficultySelectionPageContainer VBox from having the same width as its parent container (difficultySelectionPage)
        mapSelectionPage.setFillWidth(false);

        mapSelectionPageContainer.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-pref-height: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.75), Main.mainStage.heightProperty().multiply(0.6), Main.mainStage.widthProperty().multiply(0.02))
        );

        mapSelectionPageContainer_top_leftChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.2))
        );

        backBtn.styleProperty().bind(
                Bindings.format("-fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );

        mapSelectionPageContainer_top_middleChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx", Main.mainStage.widthProperty().multiply(0.6))
        );

        mapSelectionPage_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.04))
        );

        mapSelectionPageContainer_top_rightChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.2))
        );

        mapSelectionPageContainer_bottom.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.55))
        );



        rectangleMapSelectorContainer.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.25), Main.mainStage.widthProperty().multiply(0.01))
        );

        rectangleMapSelectorName.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );


        bombMapSelectorContainer.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.25), Main.mainStage.widthProperty().multiply(0.01))
        );

        bombMapSelectorName.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );


        faceMapSelectorContainer.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.25), Main.mainStage.widthProperty().multiply(0.01))
        );

        faceMapSelectorName.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );

        if (APP_CACHE.getGameDifficulty().equals("Medium")) {
            face_map_img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bombland/Images/poker-face.png")));
            face_map_imgView.setImage(face_map_img);
        }
        else if (APP_CACHE.getGameDifficulty().equals("Hard")) {
            face_map_img = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/bombland/images/sad-face.png")));
            face_map_imgView.setImage(face_map_img);
        }


        octagonMapSelectorContainer.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.25), Main.mainStage.widthProperty().multiply(0.01))
        );

        octagonMapSelectorName.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );


        mapSelectionPageContainer.setFillWidth(false);
        upperMapsContainer.setFillHeight(false);
        lowerMapsContainer.setFillHeight(false);

        VBox.setVgrow(mapSelectionPageContainer_bottom, Priority.ALWAYS);
        VBox.setVgrow(upperMapsContainer, Priority.ALWAYS);
        VBox.setVgrow(lowerMapsContainer, Priority.ALWAYS);

        HBox.setHgrow(space1, Priority.ALWAYS);
        HBox.setHgrow(space2, Priority.ALWAYS);
    }


    @FXML
    private void goToDifficultySelection() throws IOException {
        ScreenController screenController = new ScreenController(mapSelectionPage.getScene());
        screenController.addScreen("difficulty-selection", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/difficulty-selection-view.fxml"))));
        screenController.activate("difficulty-selection");
    }

    @FXML
    private void pickRectangleMap() throws IOException {
        APP_CACHE.setGameMap("Rectangle");
        startGame();
    }

    @FXML
    private void pickBombMap() throws IOException {
        APP_CACHE.setGameMap("Bomb");
        startGame();
    }

    @FXML
    private void pickFaceMap() throws IOException {
        APP_CACHE.setGameMap("Face");
        startGame();
    }

    @FXML
    private void pickOctagonMap() throws IOException {
        APP_CACHE.setGameMap("Octagon");
        startGame();
    }

    @FXML
    private void startGame() throws IOException {
//        ScreenController screenController = new ScreenController(mapSelectionPage.getScene());
//        screenController.addScreen("play", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/play-view.fxml"))));
//        screenController.activate("play");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bombland/FXML/play-view.fxml"));

        PlayController playController = PlayController.getInstance();
        loader.setController(playController);

        Scene scene = new Scene(loader.load(), 1024, 768);
        Main.mainStage.setScene(scene);
        Main.mainStage.show();
    }
}