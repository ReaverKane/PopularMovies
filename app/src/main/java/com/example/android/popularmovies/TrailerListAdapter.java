package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.objectModels.Trailer;

import java.util.ArrayList;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder> {

    final private ItemClickListener mClickListener;
    private final ArrayList<Trailer> mTrailerList;
    private final Context mContext;

    public TrailerListAdapter(Context context, ArrayList<Trailer> trailerList, ItemClickListener listener) {
        this.mTrailerList = trailerList;
        this.mContext = context;
        this.mClickListener = listener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Trailer currentTrailer = mTrailerList.get(position);
        String cTitle = currentTrailer.getTrailerName();
        holder.mTitleView.setText(cTitle);
    }

    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    public interface ItemClickListener {
        void onItemClick(int Index, View view);
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView mTitleView;
        final ImageView mShareButton;

        TrailerViewHolder(View itemView) {
            super(itemView);
            this.mTitleView = itemView.findViewById(R.id.tv_trailer_title);
            this.mShareButton = itemView.findViewById(R.id.iv_share_icon);
            mShareButton.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListener.onItemClick(position, v);
        }
    }
}
