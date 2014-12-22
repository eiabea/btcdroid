package com.eiabea.btcdroid.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.RoundsListAdapter;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

public class RoundsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ROUNDS_STATS_LOADER_ID = 444;
    private ViewGroup rootView;

    private ExpandableListView exlvRoundsHolder;

    public static RoundsFragment create() {
        RoundsFragment fragment = new RoundsFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView()");
        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_rounds, null);

        initUi(inflater, rootView);

        getActivity().getSupportLoaderManager().initLoader(ROUNDS_STATS_LOADER_ID, null, this);

        return rootView;
    }

    @SuppressLint("NewApi")
    private void initUi(LayoutInflater inflater, ViewGroup rootView) {

//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//
//        width = metrics.widthPixels;

        exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);

//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            exlvRoundsHolder.setIndicatorBounds(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
//        } else {
//            exlvRoundsHolder.setIndicatorBoundsRelative(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
//        }

    }

    private void setStats(Stats stats) {

        try {
            RoundsListAdapter adapter = new RoundsListAdapter(getActivity(), App.parseBlocks(stats.getBlocks()));

            exlvRoundsHolder.setAdapter(adapter);
        } catch (NullPointerException ignore) {

        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {


        String selection = Stats._ID + "=?";
        String[] selectionArgs = {"1"};

        return new CursorLoader(getActivity(), Stats.CONTENT_URI, null, selection, selectionArgs, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {

            switch (loader.getId()) {
                case ROUNDS_STATS_LOADER_ID:
                    c.moveToFirst();

                    Stats stats = new Stats(c);
                    stats = App.getInstance().gson.fromJson(stats.getJson(), Stats.class);

                    if (stats != null) {
                        setStats(stats);
                    }
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
