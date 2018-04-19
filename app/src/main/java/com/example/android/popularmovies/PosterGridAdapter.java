package com.example.android.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.popularmovies.objectModels.Movie;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Kane on 07/03/2018.
 * Adapter for the Grid View, showing the Poster Image from the JSON results Array as items on the
 * GridView
 */

class PosterGridAdapter extends ArrayAdapter<Movie> {

    //Declaring Variables
    private final Context mContext;
    private final ArrayList<Movie> mMovies;
    private final LayoutInflater inflater;

    //Public constructor for PosterGridAdapter
    public PosterGridAdapter(@NonNull Context context, ArrayList<Movie> movies) {
        super(context, R.layout.item_poster, movies);
        this.mContext = context;
        this.mMovies = movies;
        this.inflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        //If convertView is null, setup the layout, otherwise it will just recycle it.
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_poster, parent, false);

            holder = new ViewHolder();

            holder.imageView = convertView.findViewById(R.id.item_image);
            holder.progressBar = convertView.findViewById(R.id.item_progress);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //Set the image description to the Title of the Movie for Accessibility
        Movie movie = mMovies.get(position);
        holder.imageView.setContentDescription(movie.getTitle());

        String imagePath = movie.getPosterPath();
        String imageUrl = NetworkUtils.getImageUrl(imagePath, NetworkUtils.POSTER_IMAGE);

        Picasso
                .with(mContext)
                .load(imageUrl)
                .error(R.drawable.imagenotfound)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //When picasso gets the callback that image was loaded successfully set the
                        //Visibility of the ProgressBar to GONE
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //When picasso gets the callback that there was a connection error, also set the
                        //Visibility of the ProgressBar to GONE
                        //(it will also show a "error image" with .error
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });


        return convertView;
    }

    //ViewHolder class to hold the views for the Adapter
    static class ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
    }

}
