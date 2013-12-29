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

	private ViewGroup rootView;

	private Profile profile;

	private ExpandableListView exlvWOrkerHolder;

	private WorkerListAdapter adapter;

	private DisplayMetrics metrics;
	private int width;

	public static WorkerFragment create(int pageNumber) {
		WorkerFragment fragment = new WorkerFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");
		setRetainInstance(true);
		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_worker, null);

		initUi(inflater, rootView);

		if (profile != null) {
			setProfile(profile);
		}

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
		if (exlvWOrkerHolder != null && this.profile != null) {
			fillUpProfile();
		}

	}

	private void fillUpProfile() {
		ArrayList<Worker> list = profile.getWorkersList();

		Collections.sort(list, new App.WorkerSorter());

		adapter = new WorkerListAdapter(getActivity());

		adapter.setData(list);

		exlvWOrkerHolder.setAdapter(adapter);

		expandAllActiveWorker();
	}

	private void expandAllActiveWorker() {
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			Holder holder = (Holder) adapter.getGroup(i);
			if (holder.getWorker().isAlive()) {
				exlvWOrkerHolder.expandGroup(i);
			}

		}
	}

}
