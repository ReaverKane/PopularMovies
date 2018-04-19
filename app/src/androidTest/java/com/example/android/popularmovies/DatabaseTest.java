package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieDbHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import static org.junit.Assert.*;

/**
 * Relied heavily in the test method from the exercises
 */

@SuppressWarnings("ALL")
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    private final Class mDbHelperClass = MovieDbHelper.class;

    @Before
    public void cleanSlate(){
    deleteDatabase();
    }

    @Test
    public void test_creatingDB () throws Exception{
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class)
                .newInstance(mContext);

        SQLiteDatabase dbase = dbHelper.getWritableDatabase();

        String databaseIsNotOpen = "The database should be open, but isn't";
        assertEquals(databaseIsNotOpen,
                true,
                dbase.isOpen());

        Cursor tableNameCursor = dbase.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        MovieContract.MovieEntry.TABLE_NAME + "'", null);

        String errorCreatingDB = "Error: This means that the DB wasn't created properly";
        assertTrue(errorCreatingDB,
                tableNameCursor.moveToFirst());

        assertEquals("Error: DB was created without the expected tables.",
                MovieContract.MovieEntry.TABLE_NAME, tableNameCursor.getString(0));

        tableNameCursor.close();
    }

    @Test
    public void insert_entry_test() throws Exception{
        SQLiteOpenHelper dbHelper =
                (SQLiteOpenHelper) mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, "123");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "testTitle");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, "testPoster");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP, "testPoster");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, 1.3);
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE, "1984");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, "TEST");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_GENRES, "1, 2, 3");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TAGLINE, "TAGLINE");
        testValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RUNTIME, 123);

        long firstRowId = database.insert(MovieContract.MovieEntry.TABLE_NAME,
                null,
                testValues);
        assertNotEquals("Unable to insert into the database",
                -1,
                firstRowId);

        Cursor cursor = database.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        String emptyQueryError = "Error: No records returned from query";
        assertTrue(emptyQueryError,
                cursor.moveToFirst());
        cursor.close();
        dbHelper.close();

    }




    private void deleteDatabase(){
        try{
            Field f = mDbHelperClass.getDeclaredField("DATABASE_NAME");
            f.setAccessible(true);
            mContext.deleteDatabase((String)f.get(null));
        }catch (NoSuchFieldException nsf){
            fail("No DBName found");
        }catch (Exception ex){
            fail(ex.getMessage());
        }
    }
}
