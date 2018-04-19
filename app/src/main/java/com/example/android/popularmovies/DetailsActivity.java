package com.example.android.popularmovies;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.objectModels.DetailsLoader;
import com.example.android.popularmovies.objectModels.Movie;
import com.example.android.popularmovies.objectModels.MovieDetails;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import static java.lang.String.valueOf;

public class DetailsActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks,
        View.OnClickListener {

    //Key for the bundle passed to fragments
    public static final String BUNDLE_KEY = "MovieID";
    //Constants
    private static final String TAG = "DetailsActivity";
    //Loader ID for Details
    private static final int DETAIL_LOADER_ID = 99;
    private static final int CURSOR_LOADER_ID = 65;

    //Fragment IDs, used to pass current fragments onSaveInstanceState and to control which
    //fragment is loaded by default (changing the mCurrentFragment variable achieves this)
    private static final int TRAILERS_FRAGMENT = 8;
    private static final int REVIEWS_FRAGMENT = 4;
    private static final int CAST_FRAGMENT = 5;
    //Keys used to pass state variables to outState (onSaveInstanceState outState)
    private static final String CURRENT_FRAGMENT_SAVE_KEY = "CurrentFragment";
    private static final String CURRENT_POSITION_SAVE_KEY = "CurrentScrollPosition";

    //Variables
    //Movie Object to extract data from
    private Movie mCurrentMovie;
    //Sort Order passed from Main, used to ascertain the behaviour of the UI (Loads from html or db)
    private String mSortOrder;
    //LoaderManager
    private LoaderManager mLoaderManager;

    //Views
    private TextView titleView;
    private ImageView backgroundView;
    private TextView releaseDateView;
    private TextView ratingView;
    private TextView synopsisView;
    private ImageView posterView;
    private TextView taglineView;
    private TextView runtime;
    private TextView genresView;
    private TextView genresTag;
    private TextView castButton;
    private TextView trailersButton;
    private TextView reviewsButton;
    private ImageButton favouritesButton;
    private LinearLayout buttonHolder;
    private FrameLayout fragmentHolder;
    private TextView emptyView;
    NestedScrollView mScrollView;

    //Database Primary key value for our movie
    private long sqlID;

    //Bundle used to pass moveID to fragments
    private final Bundle mIDBundle = new Bundle();

    //State variables
    //Current Fragment (default = 8 -> Trailers)
    private int mCurrentFragment = 8;
    //Favourite Button state (Default not favourite)
    private boolean isFavourite = false;

    //MovieObject Components
    private String mMovieTitle;
    private String mMovieID;
    private String mPosterURL;
    private String mBackgroundURL;
    private double mRating;
    private String mReleaseDate;
    private String mSynopsis;
    private String mGenres;
    private String mTagLine;
    private int mRuntime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Set the action bar back button as up.
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Find views (i know about Butterknife, but for the purposes of the project i choose to
        //do it the standard way
        titleView = findViewById(R.id.tv_movie_title);
        backgroundView = findViewById(R.id.iv_backdrop);
        releaseDateView = findViewById(R.id.tv_release_date);
        ratingView = findViewById(R.id.tv_rating);
        synopsisView = findViewById(R.id.tv_synopsis);
        posterView = findViewById(R.id.iv_poster);
        taglineView = findViewById(R.id.tv_tagline);
        runtime = findViewById(R.id.tv_runtime);
        genresView = findViewById(R.id.tv_genres);
        genresTag = findViewById(R.id.tv_genres_tag);
        castButton = findViewById(R.id.tv_cast_button);
        trailersButton = findViewById(R.id.tv_trailer_button);
        reviewsButton = findViewById(R.id.tv_reviews_button);
        favouritesButton = findViewById(R.id.btn_favourites);
        buttonHolder = findViewById(R.id.button_holder);
        fragmentHolder = findViewById(R.id.fragment_holder);
        mScrollView = findViewById(R.id.sv_main_scroll);

        emptyView = findViewById(R.id.no_internet_view);
        emptyView.setVisibility(View.GONE);


        //Set OnclickListeners on the buttons
        castButton.setOnClickListener(this);
        trailersButton.setOnClickListener(this);
        reviewsButton.setOnClickListener(this);
        favouritesButton.setOnClickListener(this);
        emptyView.setOnClickListener(this);

        //get LoaderManager
        mLoaderManager = getLoaderManager();

        //Scroll position, initiate to 0
        int scrollPosition = 0;

        //Check for saved state and get the fragment type that was Active when OnSavedInstanceState
        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.getInt(CURRENT_FRAGMENT_SAVE_KEY);
            scrollPosition = savedInstanceState.getInt(CURRENT_POSITION_SAVE_KEY);
        }
        //Get the passed intent and check if it has the proper extra, if not, show a error toast
        //and close the activity
        Intent intent = getIntent();

        if (!(intent.hasExtra(MainActivity.CURRENT_MOVIE))) {
            returnOnError();
        }
        mCurrentMovie = intent.getParcelableExtra(MainActivity.CURRENT_MOVIE);

        mSortOrder = getSortOrderFromPref();

        checkDisplay();

        //Scroll to top. I tried restoring the scroll position,
        // but it's too inconsistent, probably due to the Fragments botching everything up.
        // I tried also saving state in the fragment to no avail.
        //This way at least the behaviour is consistent
        scrollToPosition(scrollPosition);
    }

    private void returnOnError() {
        Toast.makeText(this, getResources().getString(R.string.toast_intent_error), Toast.LENGTH_LONG).show();
        this.finish();
    }

    private void scrollToPosition(int y){
        final int yPos = y;
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, yPos);
            }
        });
    }

    private void checkDisplay() {
        // If sortOrder is favourite Movies get movie data from Extended Movie object
        if (mSortOrder.equals(getString(R.string.pref_favourite))) {

            Log.v(TAG, "Loading from favourites");

            // TODO Get Movie data from extended Movie
            mMovieTitle = mCurrentMovie.getTitle();
            mMovieID = mCurrentMovie.getMovieID();
            mPosterURL = mCurrentMovie.getPosterPath();
            mBackgroundURL = mCurrentMovie.getBackdropPath();
            mRating = mCurrentMovie.getVoteAverage();
            mReleaseDate = mCurrentMovie.getReleaseDate();
            mSynopsis = mCurrentMovie.getSynopsis();
            mGenres = mCurrentMovie.getGenres();
            mTagLine = mCurrentMovie.getTagline();
            mRuntime = mCurrentMovie.getRuntime();


            mLoaderManager.initLoader(CURSOR_LOADER_ID, null, this);

            //Check For Connection, if device isn't connected, hide buttons and fragments
            if ((NetworkUtils.isConnected(this))) {
                //Setup the fragment with a bundle
                buttonHolder.setVisibility(View.VISIBLE);
                fragmentHolder.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                mIDBundle.putString(BUNDLE_KEY, mMovieID);
                launchCorrectFragment(mCurrentFragment, mIDBundle);
            } else {
                buttonHolder.setVisibility(View.GONE);
                fragmentHolder.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }


            //Fill the UI with data
            fillUi();


        } else {
            Log.v(TAG, "Loading from net");
            //If sortOrder isn't favourite, then needs to use Loader to get extended details
            if (mCurrentMovie != null && NetworkUtils.isConnected(this)) {

                mMovieTitle = mCurrentMovie.getTitle();
                mMovieID = mCurrentMovie.getMovieID();
                mPosterURL = mCurrentMovie.getPosterPath();
                mBackgroundURL = mCurrentMovie.getBackdropPath();
                mRating = mCurrentMovie.getVoteAverage();
                mReleaseDate = mCurrentMovie.getReleaseDate();
                mSynopsis = mCurrentMovie.getSynopsis();

                mLoaderManager.initLoader(DETAIL_LOADER_ID, null, this);
                mLoaderManager.initLoader(CURSOR_LOADER_ID, null, this);

                //Fill the UI with data from the mCurrentMovie Object
                //Create a Bundle to pass to fragments
                mIDBundle.putString(BUNDLE_KEY, mMovieID);
                //Launch the Fragment
                launchCorrectFragment(mCurrentFragment, mIDBundle);
                //Fill UI with the rest of the data
                fillUi();
                int scrollViewHeight = mScrollView.getHeight();
                Log.v(TAG,"ScrollViewHeight " + scrollViewHeight);

            } else {
                returnOnError();
            }
        }
    }

    /*This method fills the UI with data from the movie object
     * UI Consists of:
     * Background Image
     * Title
     * Poster
     * Tag Line
     * Release Date
     * Rating
     * Runtime
     * Genres List
     * Synopsis
     * Genres List
     * Tagline
     * Runtime
     *
     * Plus Fragments (Not included in this method)
     */

    private void fillUi() {

        //Assign all the data we need to local variables
        String rateString = getString(R.string.format_rating, valueOf(mRating));
        String releaseYear = Movie.formatDateShowYear(mReleaseDate);
        String formatReleaseYear = getString(R.string.format_year, releaseYear);

        //Set text variables to their textviews
        titleView.setText(mMovieTitle);
        ratingView.setText(rateString);
        releaseDateView.setText(formatReleaseYear);
        synopsisView.setText(mSynopsis);


        //load the poster. If there's an error loading, we'll show the "image not found" local drawable
        String completePosterURL = NetworkUtils.getImageUrl(mPosterURL, NetworkUtils.POSTER_IMAGE);
        Picasso
                .with(this)
                .load(completePosterURL)
                .error(R.drawable.imagenotfound)
                .into(posterView);

        //load the backdrop. In this case we'll not provide a error image, so that only the
        // app background appears in case of error.
        String completeBackdropURL = NetworkUtils.getImageUrl(mBackgroundURL, NetworkUtils.BACKGROUND_IMAGE);
        Picasso
                .with(this)
                .load(completeBackdropURL)
                .into(backgroundView);

        if (mGenres == null || mGenres.isEmpty()) {
            genresTag.setVisibility(View.GONE);
            genresView.setVisibility(View.GONE);
        } else {
            genresTag.setVisibility(View.VISIBLE);
            genresView.setVisibility(View.VISIBLE);
            genresView.setText(mGenres);
        }

        if (mRuntime == 0) {
            runtime.setVisibility(View.INVISIBLE);
        } else {
            runtime.setVisibility(View.VISIBLE);
            //            Format the value into a String: Runtime: <value> mins.
            String formatRuntime = getString(R.string.format_runtime, valueOf(mRuntime));
            //Set the text into the view
            runtime.setText(formatRuntime);
        }

        if (mTagLine == null || mTagLine.isEmpty()) {
            taglineView.setVisibility(View.INVISIBLE);
        } else {
            taglineView.setVisibility(View.VISIBLE);
            String formatTagline = getString(R.string.format_tagline, mTagLine);
            taglineView.setText(formatTagline);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAIL_LOADER_ID:
                return new DetailsLoader(this, mMovieID);

            case CURSOR_LOADER_ID:
                String[] projection = {
                        MovieEntry._ID,
                        MovieEntry.COLUMN_MOVIE_ID,
                        MovieEntry.COLUMN_MOVIE_TITLE};
                String selection = MovieEntry.COLUMN_MOVIE_ID + "=?";
                String[] selectionArgs = {mMovieID};

                return new CursorLoader(
                        this,
                        MovieEntry.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        null);
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        switch (id) {
            case DETAIL_LOADER_ID:
                MovieDetails mMovieDetails = (MovieDetails) data;

                if (mMovieDetails != null) {
                    //get the Runtime Value
                    mRuntime = mMovieDetails.getRuntime();
                    Log.v(TAG, "Runtime " + mRuntime);
                    //Get the tagline
                    mTagLine = mMovieDetails.getTagLine();
                    //Get the genres String
                    mGenres = mMovieDetails.getGenres();
                }

                fillUi();
                break;

            case CURSOR_LOADER_ID:
                Cursor cursor = (Cursor) data;
                isFavourite = (cursor != null && cursor.getCount() > 0);
                Log.v(TAG, "Movie is Favourite? " + isFavourite);
                if (isFavourite && cursor.moveToFirst()) {
                    int titleCI = cursor.getColumnIndex(MovieEntry.COLUMN_MOVIE_TITLE);
                    int idCI = cursor.getColumnIndex(MovieEntry._ID);

                    String dbTitle = cursor.getString(titleCI);
                    sqlID = cursor.getLong(idCI);
                    Log.v(TAG, dbTitle + "Is in DB with key = " + sqlID);
                }
                checkFavourite();
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        int id = loader.getId();
        switch (id) {
            case DETAIL_LOADER_ID:
                fillUi();
                break;

            case CURSOR_LOADER_ID:
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int viewID = v.getId();

        switch (viewID) {
            case R.id.tv_cast_button:
                launchCorrectFragment(CAST_FRAGMENT, mIDBundle);
                break;

            case R.id.tv_trailer_button:
                launchCorrectFragment(TRAILERS_FRAGMENT, mIDBundle);
                break;

            case R.id.tv_reviews_button:
                launchCorrectFragment(REVIEWS_FRAGMENT, mIDBundle);
                break;

            case R.id.btn_favourites:
                if (isFavourite) {
                    removeFavouriteMovie();
                } else {
                    saveFavouriteMovie();
                }
                break;
            case R.id.no_internet_view:
                checkDisplay();
                break;
            default:
                Log.v("DetailsActivity", "Something whent wrong with onclick");
        }
    }

    private void saveFavouriteMovie() {
        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_ID, mMovieID);
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovieTitle);
        values.put(MovieEntry.COLUMN_MOVIE_POSTER, mPosterURL);
        values.put(MovieEntry.COLUMN_MOVIE_BACKDROP, mBackgroundURL);
        values.put(MovieEntry.COLUMN_MOVIE_RATING, mRating);
        values.put(MovieEntry.COLUMN_MOVIE_RELEASE, mReleaseDate);
        values.put(MovieEntry.COLUMN_MOVIE_SYNOPSIS, mSynopsis);
        values.put(MovieEntry.COLUMN_MOVIE_GENRES, mGenres);
        values.put(MovieEntry.COLUMN_MOVIE_TAGLINE, mTagLine);
        values.put(MovieEntry.COLUMN_MOVIE_RUNTIME, mRuntime);

        Uri newUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        if (newUri == null) {
            Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, mCurrentMovie.getTitle() + getString(R.string.movie_saved),
                    Toast.LENGTH_SHORT).show();
            isFavourite = true;
            checkFavourite();
        }
    }

    private void removeFavouriteMovie() {
        Uri currentMovieUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, sqlID);
        Log.v(TAG, "Movie Uri to delete: " + currentMovieUri.toString());

        int rowsDeleted = getContentResolver().delete(currentMovieUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, getString(R.string.error_deleting), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, mCurrentMovie.getTitle() + getString(R.string.movie_deleted), Toast.LENGTH_SHORT).show();
            isFavourite = false;
            checkFavourite();
        }
    }

    private void resetButtons() {
        int defaultBackground = getResources().getColor(R.color.primaryLightColor);
        castButton.setBackgroundColor(defaultBackground);
        trailersButton.setBackgroundColor(defaultBackground);
        reviewsButton.setBackgroundColor(defaultBackground);
    }

    private void setLightBackground(View view) {
        int lightBackground = getResources().getColor(R.color.lightBackground);
        view.setBackgroundColor(lightBackground);
    }

    private void launchCorrectFragment(int currentFragment, Bundle idBundle) {

        if (NetworkUtils.isConnected(this)) {
            emptyView.setVisibility(View.GONE);
            fragmentHolder.setVisibility(View.VISIBLE);
            buttonHolder.setVisibility(View.VISIBLE);
            resetButtons();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment fragment;

            switch (currentFragment) {
                case CAST_FRAGMENT:
                    fragment = new CastListFragment();
                    setLightBackground(castButton);
                    mCurrentFragment = CAST_FRAGMENT;
                    break;
                case REVIEWS_FRAGMENT:
                    fragment = new ReviewListFragment();
                    setLightBackground(reviewsButton);
                    mCurrentFragment = REVIEWS_FRAGMENT;
                    break;
                case TRAILERS_FRAGMENT:
                    fragment = new TrailerListFragment();
                    setLightBackground(trailersButton);
                    mCurrentFragment = TRAILERS_FRAGMENT;
                    break;
                default:
                    //Show Trailers in case something goes wrong...
                    fragment = new TrailerListFragment();
                    setLightBackground(trailersButton);
                    mCurrentFragment = TRAILERS_FRAGMENT;
                    break;
            }
            fragment.setArguments(idBundle);
            transaction.replace(R.id.fragment_holder, fragment);
            transaction.commit();
        } else {
            emptyView.setVisibility(View.VISIBLE);
            buttonHolder.setVisibility(View.GONE);
            fragmentHolder.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int scrollY = mScrollView.getScrollY();
        int scrollViewHeight = mScrollView.getHeight();
        Log.v(TAG,"ScrollViewHeight" + scrollViewHeight);
        outState.putInt(CURRENT_POSITION_SAVE_KEY, scrollY);
        Log.v(TAG, "Saved Position " + scrollY);
        outState.putInt(CURRENT_FRAGMENT_SAVE_KEY, mCurrentFragment);
        super.onSaveInstanceState(outState);
    }

    private void checkFavourite() {
        if (isFavourite) {
            favouritesButton.
                    setImageDrawable(getResources().getDrawable(R.drawable.ic_star_black_24dp));
        } else {
            favouritesButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_black_24dp));
        }
    }

    //This method returns the sortOrder String from the sharedPreferences
    private String getSortOrderFromPref() {
        //Default value for the sort order is "popular" stored in string R.string.pref_popular
        String defaultSort = getResources().getString(R.string.pref_popular);
        //Key value for the sort order preference is "sortOrder" stored in string
        //R.string.pref_key_sortOrder
        String sortKey = getResources().getString(R.string.pref_key_sortOrder);
        //Get the sort order from shared preferences and return the string
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = sharedPreferences.getString(sortKey, defaultSort);
        Log.v(TAG, "Sort Order: " + sortOrder);
        return sortOrder;
    }

}
