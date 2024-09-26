module com.example.bombland {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;

    opens com.example.bombland to javafx.fxml;
    exports com.example.bombland;
}