package com.example.bombland;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Objects;

public class DifficultySelectionController {
    private static DifficultySelectionController instance;

    @FXML
    Button backBtn;

    @FXML
    VBox difficultySelectionPage, difficultySelectionPageContainer, difficultySelectionPageContainer_bottom, difficultySelectionPageContainer_bottom_inner;

    @FXML
    HBox difficultySelectionPageContainer_top, difficultySelectionPageContainer_top_leftChild, difficultySelectionPageContainer_top_middleChild, difficultySelectionPageContainer_top_rightChild;

    @FXML
    Label difficultySelectionPage_title;

    @FXML
    Button easyDifficultyBtn, mediumDifficultyBtn, hardDifficultyBtn;


    private DifficultySelectionController() {}

    public static DifficultySelectionController getInstance() {
        if (instance == null) {
            instance = new DifficultySelectionController();
        }

        return instance;
    }

    @FXML
    public void initialize() {
        // Prevents the width of the difficultySelectionPageContainer VBox from having the same width as its parent container (difficultySelectionPage)
        difficultySelectionPage.setFillWidth(false);


        difficultySelectionPageContainer.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-pref-height: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.75), Main.mainStage.heightProperty().multiply(0.6), Main.mainStage.widthProperty().multiply(0.02))
        );

        difficultySelectionPageContainer_top_leftChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.2))
        );

        backBtn.styleProperty().bind(
                Bindings.format("-fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );

        difficultySelectionPageContainer_top_middleChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx", Main.mainStage.widthProperty().multiply(0.6))
        );

        difficultySelectionPage_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.04))
        );

        difficultySelectionPageContainer_top_rightChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.2))
        );


        VBox.setVgrow(difficultySelectionPageContainer_bottom, Priority.ALWAYS);

        difficultySelectionPageContainer_bottom_inner.setSpacing(Main.mainStage.heightProperty().get() * 0.04);

        easyDifficultyBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        mediumDifficultyBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        hardDifficultyBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );
    }

    @FXML
    private void goToMainMenu() throws IOException {
        ScreenController screenController = new ScreenController(difficultySelectionPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"))));
        screenController.activate("main");
    }

    @FXML
    private void pickEasyDifficulty() throws IOException {
        APP_CACHE.getInstance().setGameDifficulty("Easy");
        openMapSelectionPage();
    }

    @FXML
    private void pickMediumDifficulty() throws IOException {
        APP_CACHE.getInstance().setGameDifficulty("Medium");
        openMapSelectionPage();
    }

    @FXML
    private void pickHardDifficulty() throws IOException {
        APP_CACHE.getInstance().setGameDifficulty("Hard");
        openMapSelectionPage();
    }

    @FXML
    private void openMapSelectionPage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bombland/FXML/map-selection-view.fxml"));

        MapSelectionController mapController = MapSelectionController.getInstance();
        loader.setController(mapController);

        Scene scene = new Scene(loader.load(), 1024, 768);
        Main.mainStage.setScene(scene);
        Main.mainStage.show();
    }
}