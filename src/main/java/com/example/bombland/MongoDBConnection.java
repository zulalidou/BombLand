package com.example.bombland;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {
    private MongoClient mongoClient;
    private MongoDatabase database;

    public void connect(String connectionString, String dbName) {
        mongoClient = MongoClients.create(connectionString);
        System.out.println(mongoClient);
        database = mongoClient.getDatabase(dbName);
        System.out.println("database:");
        System.out.println(database.getCollection("Easy").countDocuments());
        System.out.println(database.getCollection("Medium").countDocuments());
        System.out.println(database.getCollection("Hard").countDocuments());
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
