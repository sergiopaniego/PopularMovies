package com.example.sergiopaniegoblanco.popularmovies;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by sergiopaniegoblanco on 11/02/2017.
 */

public class NetworkUtils {
    final static String URL_BASE="http://api.themoviedb.org/3/movie/";
    final static String PARAM_QUERY = "api_key";
    /*Here goes the API key
    final static String key="";

     */
    /**
     * Builds the URL used to query The Movie DB.
     *
     * @param option The keyword that will be queried for.
     * @return The URL to use to query The Movie DB.
     */
    public static URL buildUrl(String option) {
        Uri builtUri = Uri.parse(URL_BASE).buildUpon().appendPath(option).appendQueryParameter(PARAM_QUERY,key)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
