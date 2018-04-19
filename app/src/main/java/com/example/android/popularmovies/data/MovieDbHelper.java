package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";

    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_SQL_FAVOURITE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " ("
                + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_POSTER + " TEXT, "
                + MovieEntry.COLUMN_MOVIE_BACKDROP + " TEXT, "
                + MovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_RELEASE + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, "
                + MovieEntry.COLUMN_MOVIE_GENRES + " TEXT, "
                + MovieEntry.COLUMN_MOVIE_TAGLINE + " TEXT, "
                + MovieEntry.COLUMN_MOVIE_RUNTIME + " INTEGER"
                + ");";

        db.execSQL(CREATE_SQL_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //Reviewer feedback: use ALTER TABLE to upgrade, as per https://thebhwgroup.com/blog/how-android-sqlite-onupgrade
    }
}
