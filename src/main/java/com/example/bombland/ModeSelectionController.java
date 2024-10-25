package com.example.bombland;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Objects;

public class ModeSelectionController {
    @FXML
    Button backBtn;

    @FXML
    VBox modeSelectionPage, modeSelectionPageContainer, modeSelectionPageContainer_bottom, modeSelectionPageContainer_bottom_inner;

    @FXML
    HBox modeSelectionPageContainer_top, modeSelectionPageContainer_top_leftChild, modeSelectionPageContainer_top_middleChild, modeSelectionPageContainer_top_rightChild;

    @FXML
    Label modeSelectionPage_title;

    @FXML
    Button easyModeBtn, mediumModeBtn, hardModeBtn;


    @FXML
    public void initialize() {
        // Prevents the width of the modeSelectionPageContainer VBox from having the same width as its parent container (modeSelectionPage)
        modeSelectionPage.setFillWidth(false);


        modeSelectionPageContainer.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-pref-height: %.2fpx; -fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.75), Main.mainStage.heightProperty().multiply(0.6), Main.mainStage.widthProperty().multiply(0.02))
        );

        modeSelectionPageContainer_top_leftChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.2))
        );

        backBtn.styleProperty().bind(
                Bindings.format("-fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );

        modeSelectionPageContainer_top_middleChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx", Main.mainStage.widthProperty().multiply(0.6))
        );

        modeSelectionPage_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.04))
        );

        modeSelectionPageContainer_top_rightChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.2))
        );


        VBox.setVgrow(modeSelectionPageContainer_bottom, Priority.ALWAYS);

        modeSelectionPageContainer_bottom_inner.setSpacing(Main.mainStage.heightProperty().get() * 0.04);

        easyModeBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        mediumModeBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        hardModeBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );
    }

    @FXML
    private void goToMainMenu() throws IOException {
        ScreenController screenController = new ScreenController(modeSelectionPage.getScene());
        screenController.addScreen("main", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"))));
        screenController.activate("main");
    }

    @FXML
    private void openEasyMode() throws IOException {
        PlayController.setMode("Easy");
        startGame();
    }

    @FXML
    private void openMediumMode() throws IOException {
        PlayController.setMode("Medium");
        startGame();
    }

    @FXML
    private void openHardMode() throws IOException {
        PlayController.setMode("Hard");
        startGame();
    }

    @FXML
    private void startGame() throws IOException {
        ScreenController screenController = new ScreenController(modeSelectionPage.getScene());
        screenController.addScreen("play", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/play-view.fxml"))));
        screenController.activate("play");
    }
}