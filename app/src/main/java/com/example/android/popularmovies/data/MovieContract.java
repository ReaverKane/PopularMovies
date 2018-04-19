package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kane on 02/04/2018.
 * SQL contract for this Movie's App
 * App has a single table:
 * favourites
 * Has all the relevant information of a given movie:
 * (So basically a Movie Object + _ID)
 * _ID android default (INTEGER PRIMARY KEY AUTOINCREMENT)
 * movie_id INTEGER NOT NULL,
 * movie_title TEXT NOT NULL,
 * poster_path TEXT,
 * backdrop_path TEXT,
 * vote_average REAL NOT NULL,
 * release_date TEXT NOT NULL,
 * overview TEXT NOT NULL
 * genres TEXT
 * tagline TEXT
 * runtime INTEGER
 */

public class MovieContract {
    //Using app package name as content authority
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    //Table path
    public static final String PATH_FAVOURITES = "favourites";
    //Uri Scheme is content://
    private static final String URI_SCHEME = "content";
    //Use CONTENT_AUTHORITY to create the base of all URI's which apps will use
    //to interact with the content provider.
    public static final Uri BASE_CONTENT_URI = new Uri.Builder()
            .scheme(URI_SCHEME)
            .authority(CONTENT_AUTHORITY)
            .build();


    // To prevent someone from accidentally instantiating the contract class,
    // giving it a empty constructor
    private MovieContract() {
    }

    //Inner class that defines constant values for the Movies database table
    //Each entry on the table represents the data required to build a Movie Object.
    public static final class MovieEntry implements BaseColumns {

        //MIME type dir/list of entries
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_FAVOURITES;

        //MIME type single entry/ item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_FAVOURITES;

        //Content URI
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_FAVOURITES);


        //name of the table
        public static final String TABLE_NAME = "favourites";

        //_ID carries over from super

        //movie id
        public static final String COLUMN_MOVIE_ID = "movie_id";

        //movie title
        public static final String COLUMN_MOVIE_TITLE = "movie_title";

        //movie's poster path
        public static final String COLUMN_MOVIE_POSTER = "poster_path";

        //movie's backdrop image path
        public static final String COLUMN_MOVIE_BACKDROP = "backdrop_path";

        //movie's rating (vote average)
        public static final String COLUMN_MOVIE_RATING = "vote_average";

        //movie's release date
        public static final String COLUMN_MOVIE_RELEASE = "release_date";

        //movie's synopsis (overview)
        public static final String COLUMN_MOVIE_SYNOPSIS = "overview";

        //movie's genres
        public static final String COLUMN_MOVIE_GENRES = "genres";

        //movie's tagline
        public static final String COLUMN_MOVIE_TAGLINE = "tagline";

        //movie's runtime
        public static final String COLUMN_MOVIE_RUNTIME = "runtime";

    }

}
