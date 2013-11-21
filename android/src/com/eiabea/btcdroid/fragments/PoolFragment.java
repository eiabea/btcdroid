package com.eiabea.btcdroid.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eiabea.adapter.MainViewAdapter;
import com.eiabea.btcdroid.R;

public class PoolFragment extends Fragment {
	
	private ViewGroup rootView;
	
	public static PoolFragment create(int pageNumber) {
		PoolFragment fragment = new PoolFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pool, null);


		return rootView;
	}

	
}
