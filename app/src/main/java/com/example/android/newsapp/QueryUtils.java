package com.example.android.newsapp;


import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// Helper methods to retrieve data from Guardian to our News app
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //Store the returned success response code from server
    private static final int SUCCESS_CODE = 200;

    //Return a URL from the given url string
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e (LOG_TAG, "Problem building the URL" + e);
        }
        return url;
    }


    //Convert the InputStream into a String witch contains the whole JSON response
    private static String readFromStream (InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    //Make a HTTP request to a given URL and return the response as a String
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        //If url is null, return early
        if (url == null) {
            return jsonResponse;
        }

        //Initiate the variables with values = null
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;

        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            //If the request succeeded, read imput stream and parse the response
            if (httpURLConnection.getResponseCode() == SUCCESS_CODE) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code " + httpURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving the news JSON response " + e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (inputStream != null) {
                //Closing the input stream could throw an exception, that's why makeHttpConnection
                //specify that an IOException could be thrown
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    //Query the Guardian dataset and return a list of News objects
    public static List<News> fetchNewsData(String requestUrl) {
        //Create url object
        URL url = createUrl(requestUrl);

        //Make a HTTP request on URL and get a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP url request " + e);
        }
        //Extract relevant fields from JSON response and create a list of  News
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        return newsList;
    }

    //Return a list of News objects that were build up from parsing the given JSON response
    private static List<News> extractFeatureFromJson(String newsJson) {

        //If the JSON string is empty or null, return early
        if (TextUtils.isEmpty(newsJson)) {
            return  null;
        }

        //Create an empty ArrayList that we can star adding News to
        List<News> newsList = new ArrayList<>();

        //Try to parse the JSON response string, catch the JSONException if is thrown
        try {
            //Create a JSONObject from JSON response string
            JSONObject baseJsonObject = new JSONObject(newsJson);

            JSONObject responseObject = baseJsonObject.getJSONObject("response");

            //Create an JSONArray that store the values of key "results"
            JSONArray resultsArray = responseObject.getJSONArray("results");

            //For each JSONObject from array, create an News object
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentArticle = resultsArray.getJSONObject(i);

                //Create a JSONArray to store the value of "tags"
                JSONArray tagsArray = currentArticle.getJSONArray("tags");

                //Initialize the String with a default value
                String author = "Author name not found.";

                //Check if the key "tags" does have a author
                if (tagsArray != null && tagsArray.length() > 0) {
                    author = tagsArray.getJSONObject(0).getString("firstName")
                            + " " + tagsArray.getJSONObject(0).getString("lastName");
                    //Else, the author String will be displayed with the default initialized value
                }
                String category = currentArticle.getString("sectionName");
                String title = currentArticle.getString("webTitle");
                String webUrl = currentArticle.getString("webUrl");
                String dateAndTime = currentArticle.getString("webPublicationDate");

                //Create new object with corresponding data from JSON response
                News articles = new News(category, title,  author, webUrl, dateAndTime);

                //Add the News object to our empty List<News>
                newsList.add(articles);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Error parsing the JSON results" + e);
        }
        //Return the list of news
        return newsList;
    }
}