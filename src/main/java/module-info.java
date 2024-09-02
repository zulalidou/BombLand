module com.example.bombland {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens com.example.bombland to javafx.fxml;
    exports com.example.bombland;
}