package com.example.android.popularmovies.objectModels;

import android.net.Uri;
import android.util.Log;

/**
 * Created by Kane on 05/04/2018.
 * Creates a new Trailer Object Class
 * Contains:
 * String key
 * String name
 * String site
 */

public class Trailer {
    //Constants for URI builder
    private static final String URI_SCHEME = "https";
    private static final String URI_AUTHORITY = "www.youtube.com";
    private static final String URI_ENDPOINT = "watch";
    private static final String URI_QUERY_KEY = "v";
    //Setup the variables
    private String mTrailerKey;
    private String mTrailerName;
    private String mTrailerSite;

    //Constructor
    public Trailer(String trailerKey, String trailerName, String trailerSite) {
        this.mTrailerKey = trailerKey;
        this.mTrailerName = trailerName;
        this.mTrailerSite = trailerSite;
    }

    /**
     * @param key Key from JSON
     * @return Uri Structure:
     * https://www.youtube.com/watch?v=zIauywtvcjI
     * SCHEME// AUTHORITY / ENDPOINT / QUERY_KEY = TRAILER_KEY
     */
    public static Uri getYoutubeUri(String key) {
        Uri uri = new Uri.Builder()
                .scheme(URI_SCHEME)
                .authority(URI_AUTHORITY)
                .appendPath(URI_ENDPOINT)
                .appendQueryParameter(URI_QUERY_KEY, key)
                .build();
        Log.v("TrailerURI", uri.toString());
        return uri;
    }

    //Get and Set
    public String getTrailerKey() {
        return mTrailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.mTrailerKey = trailerKey;
    }

    public String getTrailerName() {
        return mTrailerName;
    }

    public void setTrailerName(String trailerName) {
        this.mTrailerName = trailerName;
    }

    public String getTrailerSite() {
        return mTrailerSite;
    }

    public void setTrailerSite(String trailerSite) {
        this.mTrailerSite = trailerSite;
    }
}
