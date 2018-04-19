package com.example.android.popularmovies;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.objectModels.ListLoader;
import com.example.android.popularmovies.objectModels.Review;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("unchecked")
public class ReviewListFragment extends Fragment implements LoaderManager.LoaderCallbacks<List> {

    private static final String TAG = "ReviewListFragment";
    private static final int LOADER_ID = 44;
    private RecyclerView mRecyclerView;
    private ReviewListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String mID;
    private Context mContext;
    private final ArrayList<Review> reviewList = new ArrayList<>();
    private TextView mEmptyView;
    private ProgressBar mProgressBar;


    public ReviewListFragment() {
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
            //terminate the fragment on error
            getActivity().getFragmentManager().beginTransaction().remove(this).commit();
        }
        mContext = getActivity();
        //start inflating the layout
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        //get the recyclerview
        mRecyclerView = rootView.findViewById(R.id.rv_lists);
        mEmptyView = rootView.findViewById(R.id.empty_fragment_view);
        mProgressBar = rootView.findViewById(R.id.frag_progress_bar);
        mEmptyView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
        //get the layoutmanager
        mLayoutManager = new LinearLayoutManager(mContext);
        getLoaderManager().initLoader(LOADER_ID, null, this);
        mAdapter = new ReviewListAdapter(mContext, reviewList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public Loader<List> onCreateLoader(int id, Bundle args) {
        mProgressBar.setVisibility(View.VISIBLE);
        return new ListLoader(mContext, mID, ListLoader.REVIEW_LIST_LOADER);
    }

    @Override
    public void onLoadFinished(Loader<List> loader, List data) {
        mProgressBar.setVisibility(View.GONE);
        reviewList.clear();
        if (data != null && !data.isEmpty()) {
            reviewList.addAll(data);
        } else if (data != null && data.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            mEmptyView.setText(R.string.no_reviews);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List> loader) {
        reviewList.clear();
        mAdapter.notifyDataSetChanged();
    }

}
