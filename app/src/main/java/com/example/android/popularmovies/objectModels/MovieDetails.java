package com.example.android.popularmovies.objectModels;

/**
 * Created by Kane on 06/04/2018.
 * Creates a new MovieDetails Object Class
 * Contains:
 * String[] mGenres
 * String mTagLine
 * int runtime
 */
public class MovieDetails {
    private String mGenres;
    private String mTagLine;
    private int mRuntime;

    public MovieDetails(String genres, String tagLine, int runtime) {
        this.mGenres = genres;
        this.mTagLine = tagLine;
        this.mRuntime = runtime;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String genres) {
        this.mGenres = genres;
    }

    public String getTagLine() {
        return mTagLine;
    }

    public void setTagLine(String tagLine) {
        this.mTagLine = tagLine;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRuntime(int runtime) {
        this.mRuntime = runtime;
    }
}
