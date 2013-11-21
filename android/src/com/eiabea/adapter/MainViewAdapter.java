package com.eiabea.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.fragments.PoolFragment;

public class MainViewAdapter extends FragmentPagerAdapter {

	private static final int PAGES = 2;
	
	private Context mContext;

	public MainViewAdapter(Context context, FragmentManager fm) {
		super(fm);

		this.mContext = context;
	}

	@Override
	public Fragment getItem(int position) {

		Log.i(getClass().getSimpleName(), "getItem: " + position);
		switch (position) {
		case MainActivity.FRAGMENT_POOL:
			PoolFragment poolFragment = PoolFragment.create(MainActivity.FRAGMENT_POOL);
			return poolFragment;
		case MainActivity.FRAGMENT_WORKER:
			PoolFragment poolFragment2 = PoolFragment.create(MainActivity.FRAGMENT_WORKER);
			return poolFragment2;
		}

		return null;

	}
	

	@Override
	public int getCount() {
		return PAGES;
	}

//	@Override
//	public CharSequence getPageTitle(int position) {
//		switch (position) {
//		case ShopsFragment.A_TO_Z_FRAGMENT:
//			return mContext.getResources().getString(R.string.txt_svpa_a_to_z);
//		case ShopsFragment.OPENED_FRAGMENT:
//			return mContext.getResources().getString(R.string.txt_svpa_opened);
//		case ShopsFragment.CATEGORY_FRAGMENT:
//			return mContext.getResources().getString(R.string.txt_svpa_category);
//		case ShopsFragment.NEW_FRAGMENT:
//			return mContext.getResources().getString(R.string.txt_svpa_new);
//		}
//		return null;
//	}

}
