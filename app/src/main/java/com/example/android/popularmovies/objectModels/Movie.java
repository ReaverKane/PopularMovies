package com.example.android.popularmovies.objectModels;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Kane on 05/03/2018.
 * Creates a new Movie Class
 * Contains:
 * String movieId
 * String title
 * String posterPath
 * String backdropPath
 * int voteAverage
 * String releaseDate
 * String synopsis
 */

public class Movie implements Parcelable {

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    //Setup the variables
    private String movieID;
    private String title;
    private String posterPath;
    private String backdropPath;
    private double voteAverage;
    private String releaseDate;
    private String synopsis;
    //Variables for extended Movie object
    private String tagline;
    private String genres;
    private int runtime;

    //Constructor for simplified Movie from JSON
    public Movie(String movieID, String title, String posterPath, String backdropPath, double voteAverage, String releaseDate, String synopsis) {
        Log.v("Movie", "Simple Constructor");
        this.movieID = movieID;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
    }

    //Constructor for extended Movie from database. Can't really use this one all the time
    //Since it generates too many calls to the API and it gets denied
    public Movie(String movieID, String title, String posterPath, String backdropPath, double voteAverage, String releaseDate, String synopsis, String genres, String tagline, int runtime) {
        Log.v("Movie", "Extended Constructor");
        this.movieID = movieID;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
        this.synopsis = synopsis;
        this.genres = genres;
        this.tagline = tagline;
        this.runtime = runtime;
    }

    private Movie(Parcel in) {
        Log.v("Movie", "Read Parcel, available Data = " + in.dataAvail());
        movieID = in.readString();
        Log.v("Movie", "Read MovieID, available Data = " + in.dataAvail());
        title = in.readString();
        Log.v("Movie", "Read MovieTitle, available Data = " + in.dataAvail());
        posterPath = in.readString();
        backdropPath = in.readString();
        voteAverage = in.readDouble();
        releaseDate = in.readString();
        synopsis = in.readString();
        Log.v("Movie", "Read SimpleMovie, available Data = " + in.dataAvail());
//        Check if there's more data (which means the extended object) and get the rest
        if (in.dataAvail() > 0) {
            Log.v("Movie", "Read With Extra Data");
            genres = in.readString();
            tagline = in.readString();
            runtime = in.readInt();
        }
    }

    //Format date to show only Year
    public static String formatDateShowYear(String roughDate) {

        SimpleDateFormat initFormat = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date initDate = null;
        try {
            initDate = initFormat.parse(roughDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(initDate);
    }

    //Get and set
    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(int voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {

        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.v("Movie", "Wrote Parcel");
        dest.writeString(movieID);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(backdropPath);
        dest.writeDouble(voteAverage);
        dest.writeString(releaseDate);
        dest.writeString(synopsis);
        if (genres != null) dest.writeString(genres);
        if (tagline != null) dest.writeString(tagline);
        if (runtime != 0) dest.writeInt(runtime);
    }
}
