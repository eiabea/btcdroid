package com.eiabea.btcdroid.fragments;

import android.database.Cursor;
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

public class RoundsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

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
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(ROUNDS_STATS_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView()");
        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_rounds, null);

        initUi();

        Log.i(getClass().getSimpleName(), "onCreateView() /done");

        return rootView;
    }

    private void initUi() {
        exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);
    }

    private void setStats(Stats stats) {

        Log.i(getClass().getSimpleName(), "setStats()");

        try {
            RoundsListAdapter adapter = new RoundsListAdapter(getActivity(), App.parseBlocks(stats.getBlocks()));

            exlvRoundsHolder.setAdapter(adapter);
        } catch (NullPointerException ignore) {

        }

        Log.i(getClass().getSimpleName(), "setStats() /done");

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
//                    stats = App.getInstance().gson.fromJson(stats.getJson(), Stats.class);

                    if (stats != null) {
//                        setStats(stats);
                    }
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
