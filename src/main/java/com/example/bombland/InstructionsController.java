package com.example.bombland;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.Objects;

public class InstructionsController {
    private static InstructionsController instance;

    @FXML
    VBox instructionsPage;

    @FXML
    Button backBtn;

    @FXML
    Label instructionsPage_title;

    @FXML
    HBox instructionsContainer_leftChild, instructionsContainer_middleChild, instructionsContainer_rightChild;

    @FXML
    ScrollPane instructionsScrollPane;

    @FXML
    Label section1_titleLbl;

    @FXML
    VBox imgContainer_1, imgContainer_2, imgContainer_3, imgContainer_4, imgContainer_5, imgContainer_6, imgContainer_7, imgContainer_8;

    @FXML
    ImageView imgView_1, imgView_2, imgView_3, imgView_4, imgView_5, imgView_6, imgView_7, imgView_8;

    @FXML
    Region space1, space2, space3, space4;


    private InstructionsController() {}

    public static InstructionsController getInstance() {
        if (instance == null) {
            instance = new InstructionsController();
        }

        return instance;
    }

    @FXML
    public void initialize() {
        instructionsPage.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );


        backBtn.styleProperty().bind(
                Bindings.format("-fx-background-radius: %.2fpx;", Main.mainStage.widthProperty().multiply(0.05))
        );

        instructionsPage_title.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.04))
        );

        instructionsContainer_leftChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.33))
        );

        instructionsContainer_middleChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx", Main.mainStage.widthProperty().multiply(0.34))
        );

        instructionsContainer_rightChild.styleProperty().bind(
                Bindings.format("-fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.33))
        );


        instructionsScrollPane.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx;", Main.mainStage.widthProperty().multiply(0.02))
        );

        // Makes sure the child of the scrollpane has the same width as the scrollpane
        instructionsScrollPane.setFitToWidth(true);
        VBox.setVgrow(instructionsScrollPane, Priority.ALWAYS);


        section1_titleLbl.styleProperty().bind(
                Bindings.format("-fx-font-size: %.2fpx;", Main.mainStage.widthProperty().multiply(0.03))
        );


        imgContainer_1.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_2.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_3.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_4.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_5.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_6.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_7.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );
        imgContainer_8.styleProperty().bind(
                Bindings.format("-fx-padding: %.2fpx; -fx-pref-width: %.2fpx;", Main.mainStage.widthProperty().multiply(0.01), Main.mainStage.widthProperty().multiply(0.4))
        );

        imgView_1.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_1.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_2.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_2.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_3.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_3.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_4.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_4.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_5.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_5.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_6.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_6.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_7.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_7.setFitHeight(Main.mainStage.getHeight() * 0.25);
        imgView_8.setFitWidth(Main.mainStage.getWidth() * 0.35);
        imgView_8.setFitHeight(Main.mainStage.getHeight() * 0.25);

        HBox.setHgrow(space1, Priority.ALWAYS); // Makes the space element expand
        HBox.setHgrow(space2, Priority.ALWAYS);
        HBox.setHgrow(space3, Priority.ALWAYS);
        HBox.setHgrow(space4, Priority.ALWAYS);
    }

    @FXML
    private void goToMainMenu(ActionEvent event) throws IOException {
//        ScreenController screenController = new ScreenController(instructionsPage.getScene());
//        screenController.addScreen("main", FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"))));
//        screenController.activate("main");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/bombland/FXML/main-view.fxml"));

        MainController mainController = MainController.getInstance();
        loader.setController(mainController);

        Scene scene = new Scene(loader.load(), 1024, 768);
        Main.mainStage.setScene(scene);
        Main.mainStage.show();
    }
}