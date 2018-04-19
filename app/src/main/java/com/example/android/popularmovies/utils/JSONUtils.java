package com.example.android.popularmovies.utils;

import android.text.TextUtils;

import com.example.android.popularmovies.objectModels.Actor;
import com.example.android.popularmovies.objectModels.Movie;
import com.example.android.popularmovies.objectModels.MovieDetails;
import com.example.android.popularmovies.objectModels.Review;
import com.example.android.popularmovies.objectModels.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Kane on 05/03/2018.
 * Query Tools holds helper methods that allow to retrieve and parse JSON Objects from an online
 * data server, in this case its Movies from the Movie Database to be parsed into a Movie Object
 * <p>
 * Data required For Stage 1:
 * results []>
 * id -> int
 * title -> String
 * poster_path -> String
 * backdrop_path -> String
 * vote_average -> float
 * release_date -> String
 * overview -> String
 * <p>
 * ******************************
 * <p>
 * Data for Stage 2:
 * Reviews:
 * results[]>
 * author -> String
 * content -> String
 * <p>
 * Trailers:
 * results[]>
 * key -> String
 * name -> String
 * site -> String
 * <p>
 * MovieDetails:
 * genres [] > name -> String
 * runtime -> int
 * tagline -> String
 * <p>
 * Cast:
 * cast[] >
 * character -> String
 * name -> String
 * profile_path -> String
 */

public final class JSONUtils {

    //JSON object names
    //MOVIE LIST
    private static final String RESULTS = "results";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String POSTER = "poster_path";
    private static final String BACKDROP = "backdrop_path";
    private static final String VOTE = "vote_average";
    private static final String RELEASE = "release_date";
    private static final String SYNOPSIS = "overview";
    //TRAILER LIST
    private static final String TRAILER_KEY = "key";
    private static final String TRAILER_NAME = "name";
    private static final String TRAILER_SITE = "site";
    private static final String YOUTUBE = "YouTube";
    //REVIEWS LIST
    private static final String REVIEW_AUTHOR = "author";
    private static final String REVIEW_CONTENTS = "content";
    //MOVIE DETAILS
    private static final String DETAILS_GENRES = "genres";
    private static final String DETAILS_GENRES_NAME = "name";
    private static final String DETAILS_RUNTIME = "runtime";
    private static final String DETAILS_TAGLINE = "tagline";
    //CAST
    private static final String CAST_ARRAY = "cast";
    private static final String CHARACTER_NAME = "character";
    private static final String ACTOR_NAME = "name";
    private static final String ACTOR_PROFILE = "profile_path";
    private static final String ACTOR_NUMBER = "id";

    private JSONUtils() {
    }

    // This method gets a ArrayList of Movie Objects from the JSON response
    public static ArrayList<Movie> fetchSortedMovieData(String sortOrder) {

        //Create a URL from sortOrder
        URL url = NetworkUtils.createSortedURL(sortOrder);

        //Perform the HTTP request to the URL and receive a JSON response
        String jsonResponse = null;
        try {
            jsonResponse = NetworkUtils.makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return the list of results

        return parseJSONMovieList(jsonResponse);

    }


    //This method gets a List of Trailer Objects for a specific movie via it's moveID
    public static ArrayList<Trailer> fetchTrailerList(String movieID) {

        //Create URL with movieID
        URL url = NetworkUtils.createMovieDetailURL(movieID, NetworkUtils.VIDEO_DETAILS);

        //perform the HTTP Request and get the JSON Response String
        String jsonResponse = null;
        try {
            jsonResponse = NetworkUtils.makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseJSONTrailerList(jsonResponse);
    }

    //This method gets a List of Review Objects for a specific movie via it's moveID
    public static ArrayList<Review> fetchReviewList(String movieID) {

        //Create URL with movieID
        URL url = NetworkUtils.createMovieDetailURL(movieID, NetworkUtils.REVIEW_DETAILS);

        //perform the HTTP Request and get the JSON Response String
        String jsonResponse = null;
        try {
            jsonResponse = NetworkUtils.makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseJSONReviewList(jsonResponse);
    }

    //This method gets a List of Actor Objects for a specific movie via it's movieID
    public static ArrayList<Actor> fetchCastList(String movieID) {

        //Create URL with movieID
        URL url = NetworkUtils.createMovieDetailURL(movieID, NetworkUtils.CAST_DETAILS);

        //perform the HTTP Request and get the JSON Response String
        String jsonResponse = null;
        try {
            jsonResponse = NetworkUtils.makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseJSONCastList(jsonResponse);
    }

    //This method gets a MovieDetails Object for a specific Movie via it's movieID
    public static MovieDetails fetchMovieDetails(String movieID) {
        //Create URL with movieID
        URL url = NetworkUtils.createMovieDetailURL(movieID, NetworkUtils.MOVIE_DETAILS);

        //perform the HTTP Request and get the JSON Response String
        String jsonResponse = null;
        try {
            jsonResponse = NetworkUtils.makeHTTPRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseJSONDetails(jsonResponse);
    }

    //This method extracts and parses aditional details about a movie
    private static MovieDetails parseJSONDetails(String rawJSON) {

        if (isStringBroken(rawJSON)) {
            return null;
        }

        List<String> genres = new ArrayList<>();
        int runtime = 0;
        String tagLine = null;

        try {
            JSONObject rootJSONResponse = new JSONObject(rawJSON);

            if (rootJSONResponse.has(DETAILS_GENRES)) {
                JSONArray genresArray = rootJSONResponse.getJSONArray(DETAILS_GENRES);
                for (int i = 0; i < genresArray.length(); i++) {
                    JSONObject currentGenre = genresArray.getJSONObject(i);
                    String genre = currentGenre.optString(DETAILS_GENRES_NAME);
                    genres.add(genre);
                }
            }

            if (rootJSONResponse.has(DETAILS_RUNTIME)) {
                runtime = rootJSONResponse.getInt(DETAILS_RUNTIME);
            } else runtime = 0;

            if (rootJSONResponse.has(DETAILS_TAGLINE)) {
                tagLine = rootJSONResponse.getString(DETAILS_TAGLINE);
            } else tagLine = null;

        } catch (JSONException j) {
            j.printStackTrace();
        }

        String genresString = TextUtils.join(", ", genres);


        return new MovieDetails(genresString, tagLine, runtime);
    }

    //This method extracts and parses the list of Actors
    private static ArrayList<Actor> parseJSONCastList(String rawJSON) {
        ArrayList<Actor> cast = new ArrayList<>();

        if (isStringBroken(rawJSON)) {
            return null;
        }

        try {
            JSONObject rootJSONResponse = new JSONObject(rawJSON);
            if (rootJSONResponse.has(CAST_ARRAY)) {
                JSONArray castArray = rootJSONResponse.getJSONArray(CAST_ARRAY);

                for (int i = 0; i < castArray.length(); i++) {
                    JSONObject currentActor = castArray.getJSONObject(i);

                    String character = currentActor.getString(CHARACTER_NAME);
                    String actorName = currentActor.getString(ACTOR_NAME);
                    String profile = currentActor.getString(ACTOR_PROFILE);
                    String id = currentActor.getString(ACTOR_NUMBER);

                    Actor actor = new Actor(
                            character,
                            actorName,
                            profile,
                            id);
                    cast.add(actor);
                }
            } else return null;
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return cast;
    }

    //This method extracts and parses the list of movie Trailers
    private static ArrayList<Trailer> parseJSONTrailerList(String rawJSON) {
        ArrayList<Trailer> trailerArray = new ArrayList<>();
        //Check for empty String
        if (isStringBroken(rawJSON)) {
            return null;
        }

        //Try to parse the JSON
        try {
            JSONObject rootJSONResponse = new JSONObject(rawJSON);
            if (rootJSONResponse.has(RESULTS)) {
                //extract the "results" array
                JSONArray resultsArray = rootJSONResponse.getJSONArray(RESULTS);

                //iterate the array to extract the required fields from the objects in the array
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject currentTrailer = resultsArray.getJSONObject(i);
                    String trailerKey = currentTrailer.optString(TRAILER_KEY);
                    String trailerName = currentTrailer.optString(TRAILER_NAME);
                    String trailerSite = currentTrailer.optString(TRAILER_SITE);

                    //Skip this item if site isn't youtube
                    if (!trailerSite.equals(YOUTUBE)) {
                        continue;
                    }

                    Trailer trailer = new Trailer(
                            trailerKey,
                            trailerName,
                            trailerSite);
                    trailerArray.add(trailer);
                }

            } else {
                return null;
            }
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return trailerArray;
    }

    //This method extracts and parses the list of Reviews
    private static ArrayList<Review> parseJSONReviewList(String rawJSON) {
        ArrayList<Review> reviewArray = new ArrayList<>();
        //check for empty JSON string
        if (isStringBroken(rawJSON)) {
            return null;
        }

        //Try to parse the JSON
        try {
            JSONObject rootJSONResponse = new JSONObject(rawJSON);
            if (rootJSONResponse.has(RESULTS)) {
                //get the "results" Array
                JSONArray resultArray = rootJSONResponse.getJSONArray(RESULTS);

                //iterate through the array, get the object corresponding to each entry, and get
                //the fields within each
                if (resultArray.length() >= 1) {
                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject currentReview = resultArray.getJSONObject(i);
                        String reviewAuthor = currentReview.optString(REVIEW_AUTHOR);
                        String reviewContent = currentReview.optString(REVIEW_CONTENTS);

                        Review review = new Review(
                                reviewAuthor,
                                reviewContent);
                        reviewArray.add(review);
                    }
                }
            } else return null;
        } catch (JSONException j) {
            j.printStackTrace();
        }
        return reviewArray;
    }

    //this method Extracts and Parses the sorted Movies JSON result
    private static ArrayList<Movie> parseJSONMovieList(String rawJSON) {
        ArrayList<Movie> movieArray = new ArrayList<>();

        //Check to see if we didn't get an empty or null result (from connection errors),
        //in which case we return early with a null result
        if (isStringBroken(rawJSON)) {
            return null;
        }
        //Try to Parse the JSON. If there's any problem parsing the data, throw JSONException
        //Catch the exception and Log error
        try {
            //Create a JSON object from the raw string data
            JSONObject rootJSONResponse = new JSONObject(rawJSON);

            if (rootJSONResponse.has(RESULTS)) {
                //Extract the "results" JSONArray
                JSONArray resultsArray = rootJSONResponse.getJSONArray(RESULTS);

                //for each object in the array parse a new Movie Object
                for (int i = 0; i < resultsArray.length(); i++) {
                    //get the Result at position i as a JSON Object
                    JSONObject currentMovie = resultsArray.getJSONObject(i);

                    //get the values we need
                    String movieID = currentMovie.getString(ID);
                    String movieTitle = currentMovie.getString(TITLE);

                    //Using optString in Poster and Backdrops because it can be null according to the
                    //API documentation
                    String posterPath = currentMovie.optString(POSTER);
                    String backdropPath = currentMovie.optString(BACKDROP);
                    double voteAverage = currentMovie.getDouble(VOTE);
                    String releaseDate = currentMovie.optString(RELEASE);
                    String overview = currentMovie.optString(SYNOPSIS);

                    Movie movie = new Movie(
                            movieID,
                            movieTitle,
                            posterPath,
                            backdropPath,
                            voteAverage,
                            releaseDate,
                            overview);

                    movieArray.add(movie);
                }

            } else {
                return null;
            }

        } catch (JSONException j) {
            j.printStackTrace();
        }
        return movieArray;
    }

    //this method checks if the rawJSON string is empty or null
    private static boolean isStringBroken(String rawJSON) {
        return rawJSON == null || rawJSON.isEmpty();
    }
}
