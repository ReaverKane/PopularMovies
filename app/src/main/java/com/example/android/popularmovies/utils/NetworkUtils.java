package com.example.android.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Kane on 05/03/2018.
 * This class contains Helper methods to handle URL construction and HTTP requests
 * used the httprequest method from the Android Dev Udacity exercises,
 * since it seems more concise than the one i knew (from Android Basics course mainly)
 */

public class NetworkUtils {
    //Constants for image type
    public static final int POSTER_IMAGE = 1;
    public static final int BACKGROUND_IMAGE = 2;
    public static final int VIDEO_DETAILS = 4488;
    public static final int REVIEW_DETAILS = 4499;
    public static final int MOVIE_DETAILS = 4400;
    public static final int CAST_DETAILS = 4455;
    private static final String TAG = NetworkUtils.class.getSimpleName();
    //Base URL to build the Query from
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_QUERY_KEY = "api_key";
    private static final String APPEND_VIDEOS = "videos";
    private static final String APPEND_REVIEWS = "reviews";
    private static final String APPEND_CREDITS = "credits";


    private NetworkUtils() {
    }

    //The following methods create a Java.net.URL from a string and catches a malformed URL exception
    //Create the URL to retrieve the movie list elements
    static URL createSortedURL(String sortOrder) {

        Uri requestUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(sortOrder)
                //API Key from Gradle properties
                .appendQueryParameter(API_QUERY_KEY, BuildConfig.API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(requestUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URL " + url);
        return url;
    }

    //Create the URL to retrieve a single movie's details
    public static URL createMovieDetailURL(String movieID, int type) {

        //URL structure: https://api.themoviedb.org/3/movie/19404/reviews?api_key=<API_KEY>
        //Translates to: BASE_URL/movieID/<TYPE>&<API Key query>
        Uri.Builder requestUri = Uri
                .parse(BASE_URL)
                .buildUpon()
                .appendPath(movieID);

        //TODO For logging purposes, remove later
        String detailType;
        switch (type) {
            //Create a Trailer (video) request URL
            case VIDEO_DETAILS:
                requestUri
                        .appendPath(APPEND_VIDEOS);

                //TODO For logging purposes, remove later
                detailType = APPEND_VIDEOS;
                break;
            //Create a Reviews request URL
            case REVIEW_DETAILS:
                requestUri
                        .appendPath(APPEND_REVIEWS);
                //TODO For logging purposes, remove later
                detailType = APPEND_REVIEWS;
                break;

            case MOVIE_DETAILS:
                //TODO For logging purposes, remove later
                detailType = "movie";
                break;

            case CAST_DETAILS:
                requestUri
                        .appendPath(APPEND_CREDITS);
                //TODO For logging purposes, remove later
                detailType = APPEND_CREDITS;
                break;

            default:
                requestUri = null;
                //TODO For logging purposes, remove later
                detailType = "BAD";
        }
        requestUri.appendQueryParameter(API_QUERY_KEY, BuildConfig.API_KEY).build();

        URL url = null;
        try {
            if (requestUri == null) throw new MalformedURLException();
            url = new URL(requestUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Make an HTTP Request to the given URL and return a String (JSON Data)
    static String makeHTTPRequest(URL url) throws IOException {

        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
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
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    //Creates a proper URL string for the images from the Poster or backdrop path JSON data
    public static String getImageUrl(String path, int type) {
        //base URL for tmbd images
        final String baseURL = "http://image.tmdb.org/t/p/";
        //recommended size for phones
        String size;

        switch (type) {
            case POSTER_IMAGE:
                size = "w185";
                break;
            case BACKGROUND_IMAGE:
                size = "w780";
                break;
            default:
                size = "";
        }
        //Concatenate the path, can't use URI Builder because posterPath has a "/" so it would result
        //in erroneous URL by escaping the second "/" and creating weird characters
        return baseURL + size + path;
    }

    //Check if app is connected to the internet or connecting
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
