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

public class MapSelectionController {
    @FXML
    Button backBtn;

    @FXML
    VBox mapSelectionPage, mapSelectionPageContainer, mapSelectionPageContainer_bottom;

    @FXML
    HBox mapSelectionPageContainer_top, mapSelectionPageContainer_top_leftChild, mapSelectionPageContainer_top_middleChild, mapSelectionPageContainer_top_rightChild;

    @FXML
    Label mapSelectionPage_title;


    @FXML
    public void initialize() {
        // Prevents the width of the modeSelectionPageContainer VBox from having the same width as its parent container (modeSelectionPage)
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

        VBox.setVgrow(mapSelectionPageContainer_bottom, Priority.ALWAYS);
    }


    @FXML
    private void goToModeSelection() throws IOException {
        ScreenController screenController = new ScreenController(mapSelectionPage.getScene());
        screenController.addScreen("mode-selection", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/mode-selection-view.fxml"))));
        screenController.activate("mode-selection");
    }
}
