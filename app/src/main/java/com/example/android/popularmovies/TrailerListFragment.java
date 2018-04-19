package com.example.android.popularmovies;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.objectModels.ListLoader;
import com.example.android.popularmovies.objectModels.Trailer;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Adapted a bit from example:
 * https://developer.android.com/samples/RecyclerView/src/com.example.android.recyclerview/RecyclerViewFragment.html
 */
@SuppressWarnings("unchecked")
public class TrailerListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List>,
        TrailerListAdapter.ItemClickListener {

    private static final String TAG = "TrailerListFragment";
    private static final int LOADER_ID = 88;
    private RecyclerView mRecyclerView;
    private TrailerListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mID;
    private Context mContext;
    private final ArrayList<Trailer> trailersList = new ArrayList<>();
    private TextView mEmptyView;
    private ProgressBar mProgressBar;


    public TrailerListFragment() {
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

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        //get the recycler view
        mRecyclerView = rootView.findViewById(R.id.rv_lists);
        mEmptyView = rootView.findViewById(R.id.empty_fragment_view);
        mProgressBar = rootView.findViewById(R.id.frag_progress_bar);
        mEmptyView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        //get LayoutManager as LinearLayoutManager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mContext = getActivity();
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mAdapter = new TrailerListAdapter(mContext, trailersList, this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }


    @Override
    public void onItemClick(int index, View view) {
        long viewID = view.getId();
        Trailer currentTrailer = trailersList.get(index);
        String trailerKey = currentTrailer.getTrailerKey();
        Uri trailerURL = Trailer.getYoutubeUri(trailerKey);
        String trailername = currentTrailer.getTrailerName();
        if (viewID == R.id.iv_share_icon) {

            //Share url as text using a Send Intent
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, trailerURL.toString());
            if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(Intent.createChooser(shareIntent, "Share link using"));
            } else {
                //Show error toast
                Toast.makeText(mContext, getString(R.string.format_share_intent_error, trailername), Toast.LENGTH_SHORT).show();
            }

        } else {
            //Create an intent to open the URL, Android should realize it's a youtube video
            //and ask if it should use a browser or youtube app (if installed)
            Intent playVideoItent = new Intent(Intent.ACTION_VIEW, trailerURL);
            if (playVideoItent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(playVideoItent);
            } else {
                //Show error toast
                Toast.makeText(mContext,
                        getString(R.string.format_open_intent_error, trailername),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<List> onCreateLoader(int id, Bundle args) {
        mProgressBar.setVisibility(View.VISIBLE);
        return new ListLoader(mContext, mID, ListLoader.TRAILER_LIST_LOADER);
    }

    @Override
    public void onLoadFinished(Loader<List> loader, List data) {
        mProgressBar.setVisibility(View.GONE);
        trailersList.clear();
        if (data != null && !data.isEmpty()) {
            trailersList.addAll(data);
        } else if (data != null && data.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setText(R.string.no_trailers);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List> loader) {
        trailersList.clear();
        mAdapter.notifyDataSetChanged();

    }
}
