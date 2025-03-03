package com.aiexamhub.exam.util;


import org.springframework.stereotype.Component;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
@Component
public class NaverOcr {

    private String secret = "UlJxd1pWUWFJcGVZcFR4Zm9Sc2JBYUdVVU9Ha2FaTVI=";
    private String uri = "https://pxcmyl1ayd.apigw.ntruss.com/custom/v1/37861/84253f381306c6bbd3b6008e1070bd03c4e796acdf83fae24b0723895d5bed0f/general";


    public String sendOCRRequest(String base64img) throws Exception {

        //System.out.println(base64img);

        String jsonData = "{\n" +
                "    \"version\": \"V2\",\n" +
                "    \"requestId\": \"1234\",\n" +
                "    \"timestamp\": \"1722225600000\",\n" +
                "    \"lang\": \"ko\",\n" +
                "    \"images\": [\n" +
                "        {\n" +
                "        \"format\": \"png\",\n" +
                "        \"name\": \"demo_2\",\n" +
                "        \"data\": \""+base64img+"\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"enableTableDetection\": false\n" +
                "}";
        //System.out.println("dddddddddddddddddddddddddddddddddddddddddddddddddd");
        //System.out.println(jsonData);
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("X-OCR-SECRET", secret);
            connection.setDoOutput(true);

            // JSON 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    //System.out.println("Response : " + response.toString());
                    return response.toString();
                }
            } else {
                // 에러 응답 읽기
                try (java.io.BufferedReader br = new java.io.BufferedReader(
                        new java.io.InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        errorResponse.append(responseLine.trim());
                    }
                    System.out.println("Error Response: " + errorResponse.toString());
                    throw new Exception("Request failed. Response Code: " + responseCode + ", Error: " + errorResponse.toString());
                }
            }
        } finally {
            connection.disconnect();
        }
    }

}