package com.eiabea.btcdroid.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.WorkerListAdapter;
import com.eiabea.btcdroid.adapter.WorkerListAdapter.Holder;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class WorkerFragment extends Fragment {

	public static final String PARAM_PROFILE = "param_profile";

	private ViewGroup rootView;

	private Profile profile;

	private ExpandableListView exlvWOrkerHolder;

	private WorkerListAdapter adapter;

	private DisplayMetrics metrics;
	private int width;

	public static WorkerFragment create(Profile profile) {
		WorkerFragment fragment = new WorkerFragment();
		Bundle b = new Bundle();
		b.putParcelable(PARAM_PROFILE, profile);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");
		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_worker, null);

		this.profile = getArguments().getParcelable(PARAM_PROFILE);

		initUi(inflater, rootView);

		setProfile(profile);

		return rootView;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	private void initUi(LayoutInflater inflater, ViewGroup rootView) {

		metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;

		exlvWOrkerHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_worker_holder);

		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
			exlvWOrkerHolder.setIndicatorBounds(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
		} else {
			exlvWOrkerHolder.setIndicatorBoundsRelative(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
		}

	}

	public void setProfile(Profile profile) {
		this.profile = profile;
		fillUpProfile();
	}

	private void fillUpProfile() {
		try {
			ArrayList<Worker> list = profile.getWorkersList();

			Collections.sort(list, new App.WorkerSorter());

			adapter = new WorkerListAdapter(getActivity());

			adapter.setData(list);

			exlvWOrkerHolder.setAdapter(adapter);

			expandActiveWorker();

		} catch (NullPointerException ignore) {

		}
	}

	private void expandActiveWorker() {
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			Holder holder = (Holder) adapter.getGroup(i);
			if (holder.getWorker().isAlive()) {
				exlvWOrkerHolder.expandGroup(i);
			}

		}
	}

}
