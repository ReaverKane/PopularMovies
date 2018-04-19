package com.example.android.popularmovies;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.objectModels.Actor;
import com.example.android.popularmovies.objectModels.ListLoader;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("unchecked")
public class CastListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List>, TrailerListAdapter.ItemClickListener {

    private static final String TAG = "CastListFragment";
    private static final int LOADER_ID = 55;
    private RecyclerView mRecyclerView;
    private CastListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mID;
    private Context mContext;
    private final ArrayList<Actor> castList = new ArrayList<>();
    private TextView mEmptyView;
    private ProgressBar mProgressBar;
    private static final String SAVE_STATE_KEY = "SaveState";
    private Parcelable mSavedLayoutState;

    public CastListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get Arguments Bundle and obtain MovieID from it
        Bundle idBundle = this.getArguments();
        if (idBundle != null && !idBundle.isEmpty()) {
            mID = idBundle.getString(DetailsActivity.BUNDLE_KEY);
            Log.v(TAG, "Movie ID = " + mID);
        } else {
            Log.v(TAG, "Fragment closed due to ID Bundle Error");
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
        mContext = getActivity();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        mRecyclerView = rootView.findViewById(R.id.rv_lists);
        mEmptyView = rootView.findViewById(R.id.empty_fragment_view);
        mProgressBar = rootView.findViewById(R.id.frag_progress_bar);
        mProgressBar.setVisibility(View.GONE);
        mLayoutManager = new LinearLayoutManager(mContext);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mAdapter = new CastListAdapter(mContext, castList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null){

           mSavedLayoutState = savedInstanceState.getParcelable(SAVE_STATE_KEY);
            Log.v(TAG, "Restored State" + mSavedLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcel = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(SAVE_STATE_KEY, parcel);
        Log.v(TAG, "Saved State" + parcel);

    }

    @Override
    public Loader<List> onCreateLoader(int id, Bundle args) {
        mProgressBar.setVisibility(View.VISIBLE);
        return new ListLoader(mContext, mID, ListLoader.ACTOR_LIST_LOADER);
    }

    @Override
    public void onLoadFinished(Loader<List> loader, List data) {

        mProgressBar.setVisibility(View.GONE);
        castList.clear();
        if (data != null && !data.isEmpty()) {
            castList.addAll(data);
        } else if (data != null && data.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setText(R.string.no_cast);
        }
        if (mSavedLayoutState != null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedLayoutState);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List> loader) {
        castList.clear();
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(int Index, View view) {
        Actor currentActor = castList.get(Index);
        String actorNumber = currentActor.getIdNumber();
        Uri pageUrl = Actor.getActorPage(actorNumber);
        Intent openActorPage = new Intent(Intent.ACTION_VIEW, pageUrl);
        if (openActorPage.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(openActorPage);
        } else {
            Toast.makeText(mContext,
                    getString(R.string.format_open_intent_error, "Actor Profile"),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
