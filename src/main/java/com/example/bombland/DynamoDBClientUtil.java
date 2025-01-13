package com.example.bombland;

import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.util.HashMap;
import java.util.Map;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityResponse;
import software.amazon.awssdk.services.dynamodb.model.*;


public class DynamoDBClientUtil {
    private static final String COGNITO_IDENTITY_POOL_ID = "***";
    private static CognitoIdentityClient cognitoClient;
    private static DynamoDbClient dynamoDBClient;

    static GetCredentialsForIdentityResponse credentialsResponse;


    public static void saveNewHighScore(JSONObject info, String tableName) {
        Map<String, AttributeValue> newHighScoreInfo = getNewHighScoreInfo(info);

        System.out.println("========================saveNewRecord()========================");
        System.out.println(info);
        System.out.println("========================saveNewRecord()========================\n");

        getTemporaryAWSCredentials();
        insertScoreInDB(newHighScoreInfo, tableName);
    }


    public static Map<String, AttributeValue> getNewHighScoreInfo(JSONObject info) {
        Map<String, AttributeValue> newHighScoreInfo = new HashMap<>();
        newHighScoreInfo.put("time", AttributeValue.builder().n(info.get("time").toString()).build());
        newHighScoreInfo.put("id", AttributeValue.builder().s(info.get("id").toString()).build());
        newHighScoreInfo.put("score", AttributeValue.builder().n(info.get("score").toString()).build());
        newHighScoreInfo.put("name", AttributeValue.builder().s(info.get("name").toString()).build());

        return newHighScoreInfo;
    }


    // Get temporary AWS credentials for unauthenticated users
    private static void getTemporaryAWSCredentials() {
        System.out.println("\ngetTemporaryAWSCredentials() - start");

        cognitoClient = CognitoIdentityClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try {
            GetIdRequest getIdRequest = GetIdRequest.builder()
                    .identityPoolId(COGNITO_IDENTITY_POOL_ID)
                    .build();

            GetIdResponse getIdResponse = cognitoClient.getId(getIdRequest);
            String identityId = getIdResponse.identityId();
            System.out.println("\n\nidentityId: " + identityId);


            GetCredentialsForIdentityRequest credentialsRequest = GetCredentialsForIdentityRequest.builder()
                    .identityId(identityId)  // Pass the identityId retrieved from GetId
                    .build();

            credentialsResponse = cognitoClient.getCredentialsForIdentity(credentialsRequest);


            System.out.println("\n==================================");
            System.out.println("credentials: " + credentialsResponse);
            System.out.println("==================================");
            System.out.println("Temporary Credentials: " + credentialsResponse.credentials());
            System.out.println("==================================");
        } catch (Exception err) {
            System.out.println("\nUH OH, SOMETHING WENT WRONG");
            System.out.println("--------");
            System.out.println(err);
            System.out.println("--------");
            System.out.println(err.getMessage());
            System.out.println("--------");
            System.out.println(err.getCause());
            System.out.println("===========================");
        }

        System.out.println("\ngetTemporaryAWSCredentials() - end");
    }


    // Use temporary AWS credentials to interact with DynamoDB
    private static void insertScoreInDB(Map<String, AttributeValue> newHighScoreInfo, String tableName) {
        System.out.println("\ninsertScoreInDB()\n");

        try {
            // Use the temporary credentials to create a DynamoDB client
            DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                    .region(Region.US_WEST_2)
                    .credentialsProvider(() -> AwsSessionCredentials.create(
                            credentialsResponse.credentials().accessKeyId(),
                            credentialsResponse.credentials().secretKey(),
                            credentialsResponse.credentials().sessionToken()
                    ))
                    .build();


            System.out.println("newHighScoreInfo: " + newHighScoreInfo);
            System.out.println("tableName: " + tableName);

            PutItemRequest insertNewHighScoreRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(newHighScoreInfo)
                    .build();

            dynamoDbClient.putItem(insertNewHighScoreRequest);
            System.out.println("Item saved to DynamoDB");
        } catch (Exception e) {
            System.out.println("\n\n__ERROR OCCURRED__:");
            e.printStackTrace();
        }
    }
}