package com.example.android.popularmovies;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.objectModels.ListLoader;
import com.example.android.popularmovies.objectModels.Movie;
import com.example.android.popularmovies.settings.SettingsActivity;
import com.example.android.popularmovies.utils.NetworkUtils;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks,
        SharedPreferences.OnSharedPreferenceChangeListener {
    //Keys used for Intent Extras
    static final String CURRENT_MOVIE = "Current_Movie";
    //Constants used in the Activity
    //Tag for Logging
    private static final String TAG = MainActivity.class.getSimpleName();
    //JSON_LOADER_ID is the Id for the loader used for Async tasks
    //CURRENT_MOVIE is the value for the name of the parcel to be passed to details Activity
    private static final int JSON_LOADER_ID = 32;
    private static final int CURSOR_LOADER_ID = 42;

    //Save state key for scroll position
    private static final String SCROLL_POSITION_KEY = "ScrollPosition";
    //initialize the variable to a negative value (impossible position)
    int mPosition = -1;
    //declaring UI components to be used in this activity
    private GridView gridView;
    private PosterGridAdapter mAdapter;
    private LoaderManager mLoaderManager;
    private ProgressBar mProgressBar;
    private TextView mEmptyView;
    //Variables used in the activity
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private String mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set this as the Activity context
        mContext = this;

        //find the Grid view in the layout resource
        gridView = findViewById(R.id.grid_view);
        mProgressBar = findViewById(R.id.progress_bar);
        mEmptyView = findViewById(R.id.empty_view);

        //setup the Async Loader
        mLoaderManager = getLoaderManager();

        //set-up the view adapter
        mAdapter = new PosterGridAdapter(this, new ArrayList<Movie>());
        gridView.setAdapter(mAdapter);

        //If there's a save state get the position value
        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SCROLL_POSITION_KEY);
        }


        //set an onItemClickListener to handle clicks in individual elements of the list
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //On Click execute the openDetails method,
                //and pass in the item's position in the list
                openDetails(position);
            }
        });

        //register the onSharedPreferenceChangeListener
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mSortOrder = getSortOrderFromPref();

        //Set a OnClickListener for the "error messages" so it tries to relaunch the activity.
        mEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDisplay();
            }
        });
        checkDisplay();
    }

    //after checking for connection, if app is connected initialize the async task loader
    //and change Activity title to match settings
    private void checkDisplay() {
        String prefFavourite = getString(R.string.pref_favourite);
        if (mSortOrder.equals(prefFavourite)) {
            //Juggle the views
            gridView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            //Set Empty View to "Loading..." Message
            mEmptyView.setText(R.string.loading);
            mEmptyView.setVisibility(View.VISIBLE);

            Log.v(TAG, "Loading from favourites");
            Loader listLoader = mLoaderManager.getLoader(CURSOR_LOADER_ID);
            if (listLoader == null) {
                mLoaderManager.initLoader(CURSOR_LOADER_ID, null, this);
            } else {
                mLoaderManager.restartLoader(CURSOR_LOADER_ID, null, this);
            }

            setActivityTitle();
        } else if (NetworkUtils.isConnected(mContext)) {
            Log.v(TAG, "isConnected: " + NetworkUtils.isConnected(mContext));
            //Juggle the views
            gridView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.VISIBLE);

            //Set Empty View to "Loading..." Message
            mEmptyView.setText(R.string.loading);
            mEmptyView.setVisibility(View.VISIBLE);

//            initialize the Loader restart if it already exists
            Loader listLoader = mLoaderManager.getLoader(JSON_LOADER_ID);

            if (listLoader == null) {
                mLoaderManager.initLoader(JSON_LOADER_ID, null, this);
            } else {
                mLoaderManager.restartLoader(JSON_LOADER_ID, null, this);
            }

            //Change Activity Title according to Preferences
            setActivityTitle();

        } else {
            //if it's not connected, hide the progress bar and show the Empty view with error
            gridView.setVisibility(View.INVISIBLE);
            mEmptyView.setVisibility(View.VISIBLE);
            mEmptyView.setText(R.string.no_internet);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private void openDetails(int position) {
        //Get the current movie from the adapter
        Movie currentMovie = mAdapter.getItem(position);
        //Create a new Intent to go to DetailsActivity
        Intent openDetails = new Intent(this, DetailsActivity.class);
        //Pass in the Extras
        openDetails.putExtra(CURRENT_MOVIE, currentMovie);
        //start the activity with the Intent
        startActivity(openDetails);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int scrolIndex = gridView.getFirstVisiblePosition();
        outState.putInt(SCROLL_POSITION_KEY, scrolIndex);
        Log.v(TAG, "Saved Position " + scrolIndex);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case JSON_LOADER_ID:
                return new ListLoader(mContext, mSortOrder, ListLoader.MOVIE_LIST_LOADER);
            case CURSOR_LOADER_ID:
                return new CursorLoader(
                        this,
                        MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        ArrayList<Movie> movies;

        //hide progress and emptyView
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        gridView.setVisibility(View.VISIBLE);

        //If the position isn't negative it means it was restored from save state, so we restore it
        if (mPosition >= 0) {
            Log.v(TAG, "Restored Position to " + mPosition);
            gridView.setSelection(mPosition);
        }


        //clear the adapter of previous data
        mAdapter.clear();

        switch (id) {
            case JSON_LOADER_ID:
                movies = (ArrayList<Movie>) data;
                if (movies != null && !movies.isEmpty()) {
                    mAdapter.addAll(movies);
                }
                break;

            case CURSOR_LOADER_ID:
                Cursor cursor = (Cursor) data;
                movies = new ArrayList<>();

                if (cursor.getCount() > 0) {
                    //Get Column Indexes for all data
                    int idCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_ID);
                    int titleCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);
                    int posterCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_POSTER);
                    int backgroundCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_BACKDROP);
                    int ratingCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RATING);
                    int dateCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RELEASE);
                    int synopsisCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_SYNOPSIS);
                    int genresCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_GENRES);
                    int tagCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TAGLINE);
                    int runCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_RUNTIME);

                    while (cursor.moveToNext()) {
                        String mId = cursor.getString(idCI);
                        String title = cursor.getString(titleCI);
                        String poster = cursor.getString(posterCI);
                        String background = cursor.getString(backgroundCI);
                        double rating = cursor.getDouble(ratingCI);
                        String date = cursor.getString(dateCI);
                        String synopsis = cursor.getString(synopsisCI);
                        String genres = cursor.getString(genresCI);
                        String tagline = cursor.getString(tagCI);
                        int runtime = cursor.getInt(runCI);

                        Movie movie = new Movie(
                                mId,
                                title,
                                poster,
                                background,
                                rating,
                                date,
                                synopsis,
                                genres,
                                tagline,
                                runtime);
                        movies.add(movie);
                    }
                    mAdapter.addAll(movies);
                } else {
                    mEmptyView.setVisibility(View.VISIBLE);
                    mEmptyView.setText(R.string.no_favourites_saved);

                }
                break;

            default:
                break;

        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.clear();
    }

    //Inflate the options menu from the xml menu resource
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pref_menu, menu);
        return true;
    }

    //When the menu item is pressed open the settings activity

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //If item Id matches the settings button, start the settings activity
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        //Other cases return super
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        mSortOrder = getSortOrderFromPref();

        //Change Activity Title according to Preferences
        setActivityTitle();

        //Check if there's a connection when preferences are changed,
        //this alone should avoid app crashing when users change settings while offline,
        //and will probably need to create a special case when user is viewing his favourites for Stage 2
        //But for stage 1 this should handle all problems, either way i still added a check for
        //null JSON results

        checkDisplay();
    }

    //This method returns the sortOrder String from the sharedPreferences
    private String getSortOrderFromPref() {
        //Default value for the sort order is "popular" stored in string R.string.pref_popular
        String defaultSort = getResources().getString(R.string.pref_popular);
        //Key value for the sort order preference is "sortOrder" stored in string
        //R.string.pref_key_sortOrder
        String sortKey = getResources().getString(R.string.pref_key_sortOrder);
        //Get the sort order from shared preferences and return the string
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String sortOrder = sharedPreferences.getString(sortKey, defaultSort);
        Log.v(TAG, "Sort Order: " + sortOrder);
        return sortOrder;
    }

    //This method changes the activity Title depending on the sortOrder, change only happens
    //when it's changed to top rated (so title says "Top Rated Movies", keeping default otherwise.
    private void setActivityTitle() {
        //get the sortOrder from Preferences
        String topRated = getResources().getString(R.string.pref_top_rated);
        String favourite = getResources().getString(R.string.pref_favourite);

        if (mSortOrder.equals(topRated)) {
            this.setTitle(R.string.activity_title_top_rated);
        } else if (mSortOrder.equals(favourite)) {
            this.setTitle(R.string.activity_title_favourite);
        } else {
            this.setTitle(R.string.app_name);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "Resumed");
        checkDisplay();
        super.onResume();
    }
}