package com.example.android.mynewsapp;


/**
 * Credit for this class goes to the Udacity coursework for the Quake App.
 *
 */

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

/**
 * Helper methods related to requesting and receiving news data from The Guardian.
 */
public final class QueryUtils {

    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news stories to
        ArrayList<News> newsStory = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

        /**
         * Get the JSON Response Object with all the things
         * The Results array and the Fields object have duplicate entries.  The Results array includes the
         * news Section, but not the author, while author is in the Fields object.
         */
            JSONObject rootObjectResponse = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results", to get specific
            // details for our news stories.
            // which represents a list of results (or news stories).
            JSONArray resultsArray = rootObjectResponse.getJSONArray("results");

            // For each news story in the resultsArray, create an {@link News} object
            for(int i = 0; i < resultsArray.length(); i++){
                //get a single news story at position i within the list of news stories:
                JSONObject currentNews = resultsArray.getJSONObject(i); //start with i=0

                //In addition grab the Fields Object because it contains Headline, Byline,
                // firstPublicationDate, and shortUrl
                JSONObject fields = currentNews.getJSONObject("fields");

                /**
                 * declaring a String author variable in case an author is left blank from the
                 * returned json request in the fields object where byline exists.
                 */
                String author = "Unknown Contributor";
                String section = "Unknown Section";

                // Extract the value for the key called "webTitle"
                String newsTitle = currentNews.getString("webTitle");

                // Extract the value for the key called "section"
                if (currentNews.has("section")){
                    section = currentNews.getString("section");
                }

                // Credit for helping me with code goes to Charles Rowland
                if (fields.has("byline")){
                author = fields.getString("byline");
                }

                // Extract the value for the key called "WebPublicationDate"
                String date = currentNews.getString("webPublicationDate");

                // Extract the value for the key called "url"
                String url = currentNews.getString("webURL");


                // Create a new {@link News} object with the newsTitle, section, author, date,
                // and url from the JSON response.
                News news = new News(newsTitle, section, author, date, url);

                Log.i(LOG_TAG, "In query utils NewsTitle: " + newsTitle);

                //add the new {@link news} to the list of news stories
                newsStory.add(news);
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of news stories
        return newsStory;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link news}
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}
        return news;
    }
}