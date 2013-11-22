package com.eiabea.btcdroid.fragments;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.eiabea.adapter.WorkerListAdapter;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class WorkerFragment extends Fragment {

	private ViewGroup rootView;

	private Profile profile;

	private ExpandableListView exlvWOrkerHolder;

	public static WorkerFragment create(int pageNumber) {
		WorkerFragment fragment = new WorkerFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_worker, null);

		initUi(inflater, rootView);

		if (profile != null) {
			setProfile(profile);
		}

		return rootView;
	}

	private void initUi(LayoutInflater inflater, ViewGroup rootView) {
		exlvWOrkerHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_worker_holder);
	}

	public void setProfile(Profile profile) {

		this.profile = profile;
		if (exlvWOrkerHolder != null && this.profile != null) {
			fillUpProfile();
		}

	}

	private void fillUpProfile() {
		ArrayList<Worker> list = profile.getWorkersList();

		Collections.sort(list, new App.sortWorkers());

		int totalHashrate = 0;

		WorkerListAdapter adapter = new WorkerListAdapter(getActivity());

		adapter.setData(list);

		for (Worker tmp : list) {
			// WorkerView workerView = new WorkerView(MainActivity.this);
			// workerView.setData(tmp);
			// llWorkerHolder.addView(workerView);

			totalHashrate += tmp.getHashrate();

		}
		
		((MainActivity)getActivity()).updateCurrentTotalHashrate(totalHashrate);

		exlvWOrkerHolder.setAdapter(adapter);

	}

}
