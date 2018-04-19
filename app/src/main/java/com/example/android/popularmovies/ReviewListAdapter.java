package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.objectModels.Review;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {

    private final ArrayList<Review> mReviewList;
    private final Context mContext;

    public ReviewListAdapter(Context context, ArrayList<Review> reviewList) {
        this.mContext = context;
        this.mReviewList = reviewList;
    }

    @Override
    public ReviewListAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewListAdapter.ReviewViewHolder holder, int position) {

        //Get data
        Review currentReview = mReviewList.get(position);
        String rText = currentReview.getReviewContent();
        String rAuthor = currentReview.getReviewAuthor();
        //Set to the views
        holder.mReviewContent.setText(rText);
        holder.mReviewAuthor.setText(rAuthor);

    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView mReviewContent;
        final TextView mReviewAuthor;

        ReviewViewHolder(View itemView) {
            super(itemView);
            this.mReviewContent = itemView.findViewById(R.id.tv_review_text);
            this.mReviewAuthor = itemView.findViewById(R.id.tv_review_author);
        }
    }
}
