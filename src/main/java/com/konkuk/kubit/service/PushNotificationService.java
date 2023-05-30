package com.konkuk.kubit.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

@Slf4j
@Service
public class PushNotificationService {
    private static final String PROJECT_ID = "kubit-f03ec";
    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String FCM_SEND_ENDPOINT = "/v1/projects/" + PROJECT_ID + "/messages:send";
    private static final String MESSAGING_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String[] SCOPES = { MESSAGING_SCOPE };
    public static final String MESSAGE_KEY = "message";

    private static HttpURLConnection getConnection() throws IOException {
        // [START use_access_token]
        URL url = new URL(BASE_URL + FCM_SEND_ENDPOINT);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
        // [END use_access_token]
    }

    private static String getAccessToken() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new FileInputStream("firebase/kubit-f03ec-firebase-adminsdk-84l5f-886b92a555.json"))
                .createScoped(Arrays.asList(SCOPES));
        googleCredentials.refreshAccessToken();
        return googleCredentials.getAccessToken().getTokenValue();
    }
    private static String inputstreamToString(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    public static void sendMessage(JsonObject fcmMessage) throws IOException {
        HttpURLConnection connection = getConnection();
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(fcmMessage.toString());
        outputStream.flush();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        if (responseCode == 200) {
            String response = inputstreamToString(connection.getInputStream());
            System.out.println("Message sent to Firebase for delivery, response:");
            System.out.println(response);
        } else {
            System.out.println("Unable to send message to Firebase:");
            String response = inputstreamToString(connection.getErrorStream());
            System.out.println(response);
        }
    }

    private static JsonObject buildNotificationMessage(String registerToken,String title, String body) {
        JsonObject jNotification = new JsonObject();
        jNotification.addProperty("title", title);
        jNotification.addProperty("body", body);

        JsonObject jMessage = new JsonObject();
        jMessage.add("notification", jNotification);
        /*
            firebase
            1. topic
            2. token
            3. condition -> multiple topic
         */
        // jMessage.addProperty("topic", "news");
        jMessage.addProperty("token",registerToken);

        JsonObject jFcm = new JsonObject();
        jFcm.add(MESSAGE_KEY, jMessage);

        return jFcm;
    }

    public static void sendCommonMessage(String registerToken,String title, String body) throws IOException {
        JsonObject notificationMessage = buildNotificationMessage(registerToken, title, body);
        System.out.println("FCM request body for message using common notification object:");
        prettyPrint(notificationMessage);
        sendMessage(notificationMessage);
    }

    private static void prettyPrint(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(jsonObject) + "\n");
    }
}