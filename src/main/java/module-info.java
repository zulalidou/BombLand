module com.example.bombland {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.bombland to javafx.fxml;
    exports com.example.bombland;
}