package com.eiabea.btcdroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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

	private void initUi(LayoutInflater inflater, ViewGroup rootView) {
		exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);
	}

	public void setStats(Stats stats) {

		this.stats = stats;
		if (exlvRoundsHolder != null && this.stats != null) {
			adapter = new RoundsListAdapter(getActivity(), App.parseBlocks(stats.getBlocks()));

			exlvRoundsHolder.setAdapter(adapter);
		}

	}

}
