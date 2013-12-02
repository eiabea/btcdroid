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

	private ViewGroup rootView;

	private Stats stats;

	private ExpandableListView exlvRoundsHolder;

	private RoundsListAdapter adapter;

	DisplayMetrics metrics;
	int width;

	public static RoundsFragment create(int pageNumber) {
		RoundsFragment fragment = new RoundsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_rounds, null);

		initUi(inflater, rootView);

		if (stats != null) {
			setStats(stats);
		}

		return rootView;
	}

	@SuppressLint("NewApi")
	private void initUi(LayoutInflater inflater, ViewGroup rootView) {

//		metrics = new DisplayMetrics();
//		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		width = metrics.widthPixels;

		exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);
//		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//			exlvRoundsHolder.setIndicatorBounds(width - GetDipsFromPixel(50), width - GetDipsFromPixel(0));
//		} else {
//			exlvRoundsHolder.setIndicatorBoundsRelative(width - GetDipsFromPixel(50), width - GetDipsFromPixel(0));
//		}
		
	}

	public void setStats(Stats stats) {

		this.stats = stats;
		if (exlvRoundsHolder != null && this.stats != null) {
			adapter = new RoundsListAdapter(getActivity(), App.parseBlocks(stats.getBlocks()));

			exlvRoundsHolder.setAdapter(adapter);
		}

	}

}
