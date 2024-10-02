package com.example.bombland;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class MainController {
    @FXML
    VBox mainMenuPage, mainMenuOptionsContainer;

    @FXML
    Text logoText_beforeO, logoText_afterO;

    @FXML
    Button playBtn, instructionsBtn, highScoresBtn, exitBtn;

    @FXML
    public void initialize() {
        // Prevents the width of the mainMenuOptionsContainer VBox from having the same width as its parent container (mainMenuPage)
        mainMenuPage.setFillWidth(false);

        mainMenuOptionsContainer.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-pref-height: %.2fpx;", Main.mainStage.widthProperty().multiply(0.75), Main.mainStage.heightProperty().multiply(0.6))
        );


        logoText_beforeO.styleProperty().bind(
                // Sets the font size to be 9% of the app window's width
                Bindings.format("-fx-font-size: %.2fpx; -fx-font-weight: bold;", Main.mainStage.widthProperty().multiply(0.045))
        );

        logoText_afterO.styleProperty().bind(
                // Sets the font size to be 9% of the app window's width
                Bindings.format("-fx-font-size: %.2fpx; -fx-font-weight: bold;", Main.mainStage.widthProperty().multiply(0.045))
        );


        playBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        instructionsBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        highScoresBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );

        exitBtn.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx; -fx-font-size: %.2fpx; -fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.4), Main.mainStage.widthProperty().multiply(0.03), Main.mainStage.widthProperty().multiply(0.1))
        );
    }

    @FXML
    private void openModeSelectionPage(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(mainMenuPage.getScene());
        screenController.addScreen("mode-selection", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/mode-selection-view.fxml")));
        screenController.activate("mode-selection");
    }

    @FXML
    private void openInstructionsPage(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(mainMenuPage.getScene());
        screenController.addScreen("instructions", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/instructions-view.fxml")));
        screenController.activate("instructions");
    }

    @FXML
    private void openHighScoresPage(ActionEvent event) throws IOException {
        ScreenController screenController = new ScreenController(mainMenuPage.getScene());
        screenController.addScreen("highscores", FXMLLoader.load(getClass().getResource("/com/example/bombland/FXML/high-scores-view.fxml")));
        screenController.activate("highscores");
    }

    @FXML
    private void closeApp() {
        Platform.exit();
    }
}