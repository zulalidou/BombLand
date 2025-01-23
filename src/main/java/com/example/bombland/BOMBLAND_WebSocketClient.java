package com.example.bombland;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class BOMBLAND_WebSocketClient extends WebSocketClient {
    static boolean isConnected = false;

    public BOMBLAND_WebSocketClient() throws URISyntaxException {
        super(new URI("wss://bombland-server.onrender.com/websocket/distribute-new-highscore"));
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("\nonOpen() - websocketClient class");
        isConnected = true;

        System.out.println("Connected to the WebSocket server.");
        // Send a message (e.g., a high score) when the connection is established
//        sendHighScore("1500");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("\nonMessage()");

        // Handle incoming messages from the server
        System.out.println("--> Received from server: " + message);
        System.out.println("\nmessage: " + message);

        try {
            // The message variable (sent from the server) is a stringified JSONObject object
            // that represents a new high score set on another client
            JSONObject jsonObject = new JSONObject(message);
            PlayController.updateAppCache(jsonObject);
        } catch (JSONException e) {
            System.out.println("The message from the server is a simple string\n");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("\nonClose()\n");
        System.out.println("WebSocket closed with code " + code + " and reason: " + reason);

        if (isConnected) {
            isConnected = false;
            System.out.println("Disconnected from the server.");
        }
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("\nonError()\n");
        ex.printStackTrace();
    }

    // Helper method to send a high score (or any message) to the server
    public void sendHighScore(String score) {
        System.out.println("\n===sendHighScore()===");
        System.out.println("isConnected = " + isConnected);
        System.out.println("getConnection().isOpen() = " + getConnection().isOpen() + "\n");

        if (isConnected && getConnection().isOpen()) {
            System.out.println("score: " + score);
            send(score); // creates a new thread
        } else {
            System.out.println("Connection not open. Unable to send message.");
        }
    }

    // Connect to the server
    public void connectClient() {
        System.out.println("\nconnectClient()");

        if (!isConnected) {
            isConnected = true;
            this.connect();
            System.out.println("connectClient() - end");
        }
        else {
            System.out.println("Already connected to the server.");
        }
    }

    // Close the connection when done
    public void close() {
        System.out.println("\nclose()");

        if (isConnected) {
            this.close();
            isConnected = false;
        }
    }
}
