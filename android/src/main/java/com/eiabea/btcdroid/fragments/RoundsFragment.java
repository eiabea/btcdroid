package com.eiabea.btcdroid.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.RoundsListAdapter;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

public class RoundsFragment extends Fragment {

    public static final String PARAM_STATS = "param_stats";

    private ViewGroup rootView;

    private Stats stats;

    private ExpandableListView exlvRoundsHolder;

    private RoundsListAdapter adapter;

    private DisplayMetrics metrics;
    private int width;

    public static RoundsFragment create(Stats stats) {
        RoundsFragment fragment = new RoundsFragment();
        Bundle b = new Bundle();
        b.putParcelable(PARAM_STATS, stats);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView()");
        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_rounds, null);

        this.stats = getArguments().getParcelable(PARAM_STATS);

        initUi(inflater, rootView);

        setStats(stats);

        return rootView;
    }

    @SuppressLint("NewApi")
    private void initUi(LayoutInflater inflater, ViewGroup rootView) {

        metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        width = metrics.widthPixels;

        exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            exlvRoundsHolder.setIndicatorBounds(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
        } else {
            exlvRoundsHolder.setIndicatorBoundsRelative(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
        }

    }

    public void setStats(Stats stats) {

        this.stats = stats;
        try {
            adapter = new RoundsListAdapter(getActivity(), App.parseBlocks(stats.getBlocks()));

            exlvRoundsHolder.setAdapter(adapter);
        } catch (NullPointerException ignore) {

        }

    }
}
