package com.isch.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.isch.Config.Constant;
import com.isch.models.Request;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/api/isch")
public class SessionController {

    @PostMapping
    public ResponseEntity<StringBuilder> getResponse(@RequestBody Request request) throws Exception {
        if(request.getKey() == null || !request.getKey().equalsIgnoreCase(Constant.AUTH_KEY)){
            throw new Exception("What are you trying to do?");
        }
        StringBuilder output = new StringBuilder();
        String apiKey = Constant.API_KEY;
        String endpointurl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key="+apiKey;

        JsonObject requestJson = new JsonObject();
        JsonObject userPart = new JsonObject();
        userPart.addProperty("text", request.getInput());

        JsonObject userContent = new JsonObject();
        userContent.addProperty("role", "user");
        userContent.add("parts", userPart);

        requestJson.add("contents", userContent);
        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 1);
        generationConfig.addProperty("topK", 0);
        generationConfig.addProperty("topP", 0.95);
        generationConfig.addProperty("maxOutputTokens", 8192);
        generationConfig.addProperty("stopSequences", "");

        requestJson.add("generationConfig", generationConfig);

        JsonObject safetySettings = new JsonObject();
        safetySettings.addProperty("category", "HARM_CATEGORY_HARASSMENT");
        safetySettings.addProperty("threshold", "BLOCK_MEDIUM_AND_ABOVE");
        requestJson.add("safetySettings", safetySettings);

        URL url = new URL(endpointurl);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(requestJson.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            //System.out.println(response.toString());

            String  json = response.toString();
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray candidatesArray = jsonObject.getAsJsonArray("candidates");
            for (JsonElement candidateElement : candidatesArray) {
                JsonObject candidateObject = candidateElement.getAsJsonObject();
                JsonObject contentObject = candidateObject.getAsJsonObject("content");
                JsonArray partsArray = contentObject.getAsJsonArray("parts");
                for (JsonElement partElement : partsArray) {
                    JsonObject partObject = partElement.getAsJsonObject();
                    output.append(partObject.get("text").getAsString());
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return new ResponseEntity<>(output, HttpStatus.OK);
    }


}
