package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;

public class MovieProvider extends ContentProvider {

    //Uri Matcher code for a table of Movies
    public static final int MOVIES = 80;
    //Uri Matcher code for a single Movie
    public static final int MOVIE_SINGLE = 81;
    //CONSTANTS:
    //Tag for logging
    private static final String TAG = MovieProvider.class.getSimpleName();
    //initialize the URI matcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        //whole table/multiple rows
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITES, MOVIES);
        //Single Row
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_FAVOURITES + "/#", MOVIE_SINGLE);
    }

    //VARS
    //DB Helper
    private MovieDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase rDb = mDbHelper.getReadableDatabase();

        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                //Query the Whole database
                cursor = rDb.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case MOVIE_SINGLE:
                //We want to query a specific row through it's ID
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = rDb.query(
                        MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Cannot Query this URI: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_SINGLE:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase wDb = mDbHelper.getWritableDatabase();
        //Single case since the only destination for an insert is the whole table
        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                //The user can't interact with the data to be added, so its assumed to always be valid
                long insertedRowID = wDb.insert(MovieEntry.TABLE_NAME, null, values);

                if (insertedRowID == -1) {
                    Log.e(TAG, "Failed to insert row for: " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, insertedRowID);

            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;

        SQLiteDatabase wDb = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            //We only want to delete single rows
            case MOVIE_SINGLE:
                selection = MovieEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = wDb.delete(
                        MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        //The data added should be somewhat constant, except maybe the ratings varying, but updating the entries isn't really inside
        //the project's scope, so not going to do it, hence this method is not required atm.
        throw new RuntimeException("Update isn't supported at the moment");
    }
}
