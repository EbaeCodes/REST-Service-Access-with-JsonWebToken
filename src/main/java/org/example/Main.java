package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) {
        System.out.println("Retrieving transport list...");

        final HttpClient client = HttpClient.newHttpClient();

        Dotenv dotenv = Dotenv.load();
        String client_id = dotenv.get("CLIENT_ID");
        String client_secret = dotenv.get("CLIENT_SECRET");

        JSONObject jsonObject = new JSONObject(getAuthorizationToken(client_id, client_secret,client));
        String authorizationToken = jsonObject.getString("access_token");


        if (!authorizationToken.isEmpty()) {
            try {
                retrieveTransportList(authorizationToken,client);
            } catch (IOException | InterruptedException e) {
                System.err.println("Error retrieving transport list: " + e.getMessage());
            }
        } else {
            System.err.println("Failed to retrieve authorization token.");
        }
    }


    private static String getAuthorizationToken(String client_id, String client_secret, HttpClient client) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://mbn-provider.authentication.eu12.hana.ondemand.com/oauth/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("&grant_type=client_credentials" +
                        "&response_type=token" + "&client_id="+ client_id + "&client_secret=" +
                        client_secret + "&scope=interview_demo_transport_app!b923597.transportread"))
                .build();
        HttpResponse<String> response = null;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Authorization response: " + response.body());
        return response.body();
    }

    private static void retrieveTransportList(String authorizationToken,HttpClient client) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://interview-demo-transport-backend.cfapps.eu12.hana.ondemand.com/transports"))
                .header("Authorization", "Bearer " + authorizationToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

}
