package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.objectModels.Actor;
import com.example.android.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CastListAdapter extends RecyclerView.Adapter<CastListAdapter.CastViewHolder> {
    private final TrailerListAdapter.ItemClickListener mClickListner;
    private final ArrayList<Actor> mCastList;
    private final Context mContext;

    public CastListAdapter(Context context, ArrayList<Actor> actorList, TrailerListAdapter.ItemClickListener clickListener) {
        this.mCastList = actorList;
        this.mContext = context;
        this.mClickListner = clickListener;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_cast, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CastViewHolder holder, int position) {
        Actor currentActor = mCastList.get(position);
        String aName = currentActor.getName();
        String aProfile = currentActor.getProfilePath();
        String aCharacter = currentActor.getCharacter();
        holder.mActor.setText(aName);
        holder.mCharacter.setText(aCharacter);

        String actorPortraitPath = NetworkUtils.getImageUrl(aProfile, NetworkUtils.POSTER_IMAGE);

        Picasso
                .with(mContext)
                .load(actorPortraitPath)
                .error(R.drawable.imagenotfound)
                .into(holder.mProfile);
    }

    @Override
    public int getItemCount() {
        return mCastList.size();
    }

    class CastViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView mProfile;
        final TextView mCharacter;
        final TextView mActor;

        CastViewHolder(View itemView) {
            super(itemView);
            this.mProfile = itemView.findViewById(R.id.iv_actor_profile);
            this.mCharacter = itemView.findViewById(R.id.tv_character_name);
            this.mActor = itemView.findViewById(R.id.tv_actor_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mClickListner.onItemClick(position, v);
        }
    }
}
