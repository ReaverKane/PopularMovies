package com.example.android.popularmovies.objectModels;

import android.net.Uri;
import android.util.Log;

/**
 * Created by Kane on 06/04/2018.
 * Creates a new Actor Object Class
 * Contains:
 * String mCharacter
 * String mName
 * String profile_path
 * String idNumber
 */

public class Actor {


    //Constants for Uri Builder
    private static final String URI_SCHEME = "https";
    private static final String URI_AUTHORITY = "www.themoviedb.org";
    private static final String URI_PATH = "person";
    private String mCharacter;
    private String mName;
    private String mProfilePath;
    private String mIdNumber;

    public Actor(String mCharacter, String mName, String mProfilePath, String idNumber) {
        this.mCharacter = mCharacter;
        this.mName = mName;
        this.mProfilePath = mProfilePath;
        this.mIdNumber = idNumber;
    }

    /**
     * @param idNumber Actor ID Number from JSON
     * @return Uri, Structure:
     * https://www.themoviedb.org/person/1254583
     * SCHEME:// AUTHORITY / PATH / <ActorID>
     */
    public static Uri getActorPage(String idNumber) {
        Uri uri = new Uri.Builder()
                .scheme(URI_SCHEME)
                .authority(URI_AUTHORITY)
                .appendPath(URI_PATH)
                .appendPath(idNumber)
                .build();
        Log.v("ActorURI", uri.toString());
        return uri;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String character) {
        this.mCharacter = character;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getProfilePath() {
        return mProfilePath;
    }

    public void setProfilePath(String profilePath) {
        this.mProfilePath = profilePath;
    }

    public String getIdNumber() {
        return mIdNumber;
    }

    public void setIdNumber(String mIdNumber) {
        this.mIdNumber = mIdNumber;
    }
}
