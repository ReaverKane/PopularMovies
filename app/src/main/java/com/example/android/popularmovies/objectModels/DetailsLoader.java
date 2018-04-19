package com.example.android.popularmovies.objectModels;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.example.android.popularmovies.utils.JSONUtils;


public class DetailsLoader extends AsyncTaskLoader<MovieDetails> {

    private final String mMovieID;
    private MovieDetails mMovieDetails = null;

    public DetailsLoader(Context context, String movieID) {
        super(context);
        this.mMovieID = movieID;
    }

    @Override
    public MovieDetails loadInBackground() {
        if (mMovieID == null) return null;

//        get MovieDetails
        return JSONUtils.fetchMovieDetails(mMovieID);
//        return null;
    }

    @Override
    protected void onStartLoading() {
        if (mMovieDetails != null) {
            deliverResult(mMovieDetails);
        } else {
            forceLoad();
        }
    }

    public void deliverResult(MovieDetails details) {
        mMovieDetails = details;
        super.deliverResult(details);
    }
}
