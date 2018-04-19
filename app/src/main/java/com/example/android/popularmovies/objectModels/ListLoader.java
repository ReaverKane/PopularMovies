package com.example.android.popularmovies.objectModels;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.utils.JSONUtils;

import java.util.List;

/**
 * Created by Kane on 07/03/2018.
 * This class contains the methods for Async Loading of data from the internet
 * the result will be a List of Objects
 */

public class ListLoader extends AsyncTaskLoader<List> {

    //Loader types:
    public static final int MOVIE_LIST_LOADER = 1100;
    public static final int REVIEW_LIST_LOADER = 1144;
    public static final int ACTOR_LIST_LOADER = 1155;
    public static final int TRAILER_LIST_LOADER = 1188;
    private final String mArgument;
    private List mResults = null;
    private final int mType;


    //Constructor
    public ListLoader(Context context, String arguments, int type) {
        super(context);
        this.mArgument = arguments;
        this.mType = type;
    }

    @Override
    protected void onStartLoading() {
        if (mResults != null) {
            deliverResult(mResults);
        } else {
            forceLoad();
        }
    }

    @Override
    public List loadInBackground() {
        if (mArgument == null) return null;

        switch (mType) {
            case MOVIE_LIST_LOADER:
                //Loads sorted movie data using the helper method in JSONUtils
                return JSONUtils.fetchSortedMovieData(mArgument);
            case ACTOR_LIST_LOADER:
                //Loads cast list data using the helper method in JSONUtils
                return JSONUtils.fetchCastList(mArgument);
            case REVIEW_LIST_LOADER:
                //Loads Reviews data using the helper method in JSONUtils
                return JSONUtils.fetchReviewList(mArgument);
            case TRAILER_LIST_LOADER:
                //Loads Trailer data using the helper method in JSONUtils
                return JSONUtils.fetchTrailerList(mArgument);
            default:
                return null;
        }
    }

    public void deliverResult(List results) {
        mResults = results;
        super.deliverResult(results);
    }

}
