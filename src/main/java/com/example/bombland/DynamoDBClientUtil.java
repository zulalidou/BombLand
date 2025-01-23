package com.example.bombland;

import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.dynamodb.model.*;

public class DynamoDBClientUtil {
    private static DynamoDbAsyncClient dynamodbAsyncClient;
    private static Credentials awsCredentials;

    public static void getHighScores() {
        // The AWS credentials have expired
        if (awsCredentials == null || awsCredentials.expiration().isBefore(Instant.now())) {
            getTemporaryAWSCredentials();
        }

        pullHighScoresFromDB();
        sortHighScores();
        trimHighScores();
    }

    // Get temporary AWS credentials for unauthenticated users
    private static void getTemporaryAWSCredentials() {
        CognitoIdentityClient cognitoClient = CognitoIdentityClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try {
            GetIdRequest getIdRequest = GetIdRequest.builder()
                    .identityPoolId(APP_CACHE.getIdentityPoolID())
                    .build();

            GetIdResponse getIdResponse = cognitoClient.getId(getIdRequest);
            String identityId = getIdResponse.identityId();

            GetCredentialsForIdentityRequest credentialsRequest = GetCredentialsForIdentityRequest.builder()
                    .identityId(identityId)  // Pass the identityId retrieved from GetId
                    .build();

            awsCredentials = cognitoClient.getCredentialsForIdentity(credentialsRequest).credentials();
        } catch (Exception err) {
            System.out.println("===========================");
            System.out.println("\nUH OH, SOMETHING WENT WRONG");
            System.out.println(err);
            System.out.println("===========================");
        }
    }

    public static void pullHighScoresFromDB() {
        dynamodbAsyncClient = DynamoDbAsyncClient.builder()
                .region(Region.US_WEST_2)
                .credentialsProvider(() -> AwsSessionCredentials.create(
                        awsCredentials.accessKeyId(),
                        awsCredentials.secretKey(),
                        awsCredentials.sessionToken()
                ))
                .build();

        CompletableFuture<Void> allScans = CompletableFuture.allOf(
                scanTable("BOMBLAND_EasyHighScores", "Easy"),
                scanTable("BOMBLAND_MediumHighScores", "Medium"),
                scanTable("BOMBLAND_HardHighScores", "Hard")
        );

        // waits for all scans to complete
        allScans.join();

        dynamodbAsyncClient.close();
    }


    // Asynchronous scan operation for a given table
    private static CompletableFuture<Void> scanTable(String tableName, String gameMode) {
        ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();

        CompletableFuture<ScanResponse> scanResponse = dynamodbAsyncClient.scan(scanRequest);

        // Handle the scan response
        return scanResponse.thenAccept(response -> {
            List<Map<String, AttributeValue>> highScores = response.items();
            final ArrayList<JSONObject> highScoresList = new ArrayList<>();

            for (Map<String, AttributeValue> item : highScores) {
                JSONObject highScoresObj = new JSONObject();
                highScoresObj.put("id", item.get("id").s());
                highScoresObj.put("name", item.get("name").s());
                highScoresObj.put("score", item.get("score").n());
                highScoresObj.put("time", item.get("time").n());

                highScoresList.add(highScoresObj);
            }

            APP_CACHE.setHighScore(highScoresList, gameMode);
        }).exceptionally(ex -> {
            System.err.println("Scan failed for table: " + tableName);
            ex.printStackTrace();
            return null;
        });
    }


    private static void sortHighScores() {
        sortHighScoreList(APP_CACHE.getHighScores("Easy"));
        sortHighScoreList(APP_CACHE.getHighScores("Medium"));
        sortHighScoreList(APP_CACHE.getHighScores("Hard"));
    }


    private static void sortHighScoreList(ArrayList<JSONObject> highScoresList) {
        highScoresList.sort(Comparator.comparingLong(a -> a.getLong("time")));
        highScoresList.sort(Comparator.comparingLong(a -> a.getLong("score")));
    }


    private static void trimHighScores() {
        if (APP_CACHE.getHighScores("Easy").size() > 10) {
            trimHighScoreList(APP_CACHE.getHighScores("Easy"));
        }

        if (APP_CACHE.getHighScores("Medium").size() > 10) {
            trimHighScoreList(APP_CACHE.getHighScores("Medium"));
        }

        if (APP_CACHE.getHighScores("Hard").size() > 10) {
            trimHighScoreList(APP_CACHE.getHighScores("Hard"));
        }
    }

    private static void trimHighScoreList(ArrayList<JSONObject> highScoresList) {
        int startIdx = 10;
        highScoresList.subList(startIdx, highScoresList.size()).clear();
    }




    public static void saveNewHighScore(JSONObject info, String tableName) {
        Map<String, AttributeValue> newHighScoreInfo = getNewHighScoreInfo(info);

        System.out.println("========================saveNewRecord()========================");
        System.out.println(info);
        System.out.println("========================saveNewRecord()========================\n");


        // The AWS credentials have expired
        if (awsCredentials == null || awsCredentials.expiration().isBefore(Instant.now())) {
            getTemporaryAWSCredentials();
        }

        insertScoreInDB(newHighScoreInfo, tableName);
    }


    public static Map<String, AttributeValue> getNewHighScoreInfo(JSONObject info) {
        Map<String, AttributeValue> newHighScoreInfo = new HashMap<>();
        newHighScoreInfo.put("time", AttributeValue.builder().n(info.get("time").toString()).build());
        newHighScoreInfo.put("id", AttributeValue.builder().s(info.get("id").toString()).build());
        newHighScoreInfo.put("score", AttributeValue.builder().n(info.get("score").toString()).build());
        newHighScoreInfo.put("name", AttributeValue.builder().s(info.get("name").toString()).build());
        newHighScoreInfo.put("mode", AttributeValue.builder().s(info.get("mode").toString()).build());

        return newHighScoreInfo;
    }


    // Use temporary AWS credentials to interact with DynamoDB
    private static void insertScoreInDB(Map<String, AttributeValue> newHighScoreInfo, String tableName) {
        try {
            // Use the temporary credentials to create a DynamoDB client
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(Region.US_WEST_2)
                    .credentialsProvider(() -> AwsSessionCredentials.create(
                            awsCredentials.accessKeyId(),
                            awsCredentials.secretKey(),
                            awsCredentials.sessionToken()
                    ))
                    .build();

            PutItemRequest insertNewHighScoreRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(newHighScoreInfo)
                    .build();

            dynamoDbClient.putItem(insertNewHighScoreRequest);
        } catch (Exception e) {
            System.out.println("\n\n__ERROR OCCURRED__:");
            e.printStackTrace();
        }
    }
}