package com.example.fifthlesson.app.utils;

import android.util.Base64;
import android.util.Log;

import com.example.fifthlesson.app.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class TwitterUtils {

    public static final String TAG = "TwitterUtils";

    public static String appAuthentication(){

        HttpURLConnection httpConnection = null;
        OutputStream outputStream = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = null;

        try {
            URL url = new URL(ConstantsUtils.URL_AUTHENTICATION);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("POST");
            httpConnection.setDoOutput(true);
            httpConnection.setDoInput(true);

            String accessCredential = ConstantsUtils.CONSUMER_KEY + ":" + ConstantsUtils.CONSUMER_SECRET;
            String authorization = "Basic " + Base64.encodeToString(accessCredential.getBytes(), Base64.NO_WRAP);
            String param = "grant_type=client_credentials";

            httpConnection.addRequestProperty("Authorization", authorization);
            httpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            httpConnection.connect();

            outputStream = httpConnection.getOutputStream();
            outputStream.write(param.getBytes());
            outputStream.flush();
            outputStream.close();

            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String line;
            response = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null){
                response.append(line);
            }

            Log.d(TAG, "POST response code: " + String.valueOf(httpConnection.getResponseCode()));
            Log.d(TAG, "JSON response: " + response.toString());

        } catch (Exception e) {
            Log.e(TAG, "POST error: " + Log.getStackTraceString(e));

        }finally{
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return response.toString();
    }

    public static ArrayList<Tweet> getTimelineForSearchTerm(String searchTerm){

        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        HttpURLConnection httpConnection = null;
        BufferedReader bufferedReader = null;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(ConstantsUtils.URL_SEARCH + searchTerm + "&count=20");
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");

            String jsonString = appAuthentication();
            JSONObject jsonObjectDocument = new JSONObject(jsonString);
            String token = jsonObjectDocument.getString("token_type") + " " +
                    jsonObjectDocument.getString("access_token");

            httpConnection.setRequestProperty("Authorization", token);
            httpConnection.setRequestProperty("Content-Type", "application/json");
            httpConnection.connect();

            bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null){
                response.append(line);
            }

            Log.d(TAG, "GET response code: " + String.valueOf(httpConnection.getResponseCode()));
            Log.d(TAG, "JSON response: " + response.toString());

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray jsonArray = jsonResponse.getJSONArray("statuses");
            JSONObject tweetJsonObject;

            for (int i = 0; i < jsonArray.length(); i++)
            {
                tweetJsonObject = (JSONObject) jsonArray.get(i);

                Tweet tweet = new Tweet();

                tweet.setId(tweetJsonObject.getString("id_str"));
                tweet.setName(tweetJsonObject.getJSONObject("user").getString("name"));
                tweet.setScreenName(tweetJsonObject.getJSONObject("user").getString("screen_name"));
                tweet.setProfileImageUrl(tweetJsonObject.getJSONObject("user").getString("profile_image_url"));
                tweet.setText(tweetJsonObject.getString("text"));
                tweet.setCreatedAt(tweetJsonObject.getString("created_at"));

                tweets.add(i, tweet);
            }

        } catch (Exception e) {
            Log.e(TAG, "GET error: " + Log.getStackTraceString(e));

        }finally {
            if(httpConnection != null){
                httpConnection.disconnect();
            }
        }

        return tweets;
    }

}
