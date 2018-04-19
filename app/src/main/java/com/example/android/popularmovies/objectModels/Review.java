package com.example.android.popularmovies.objectModels;

/**
 * Created by Kane on 05/04/2018.
 * Creates a new Trailer Object Class
 * Contains:
 * String author
 * String content
 */

public class Review {

    //Setup the variables
    private String mReviewAuthor;
    private String mReviewContent;

    //Constructor
    public Review(String reviewAuthor, String reviewContent) {
        this.mReviewAuthor = reviewAuthor;
        this.mReviewContent = reviewContent;
    }

    //Get and Set
    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    public void setReviewAuthor(String reviewAuthor) {
        this.mReviewAuthor = reviewAuthor;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.mReviewContent = reviewContent;
    }
}
