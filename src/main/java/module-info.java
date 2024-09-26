module com.example.bombland {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.json;

    opens com.example.bombland to javafx.fxml;
    exports com.example.bombland;
}