package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.fragments.PayoutFragment;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.RoundsFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class MainViewAdapter extends FragmentPagerAdapter {

	private static final int PAGES = 4;
	
	private Context mContext;

	public MainViewAdapter(Context context, FragmentManager fm, Profile profile, Stats stats, GenericPrice price) {
		super(fm);
		
		this.mContext = context;
	}

	@Override
	public Fragment getItem(int position) {

		Log.i(getClass().getSimpleName(), "getItem: " + position);
		switch (position) {
		case MainActivity.FRAGMENT_PAYOUT:
			PayoutFragment payoutFragment = PayoutFragment.create(MainActivity.FRAGMENT_PAYOUT);
			return payoutFragment;
		case MainActivity.FRAGMENT_POOL:
			PoolFragment poolFragment = PoolFragment.create(MainActivity.FRAGMENT_POOL);
			return poolFragment;
		case MainActivity.FRAGMENT_WORKER:
			WorkerFragment workerFragment = WorkerFragment.create(MainActivity.FRAGMENT_WORKER);
			return workerFragment;
		case MainActivity.FRAGMENT_ROUNDS:
			RoundsFragment roundsFragment = RoundsFragment.create(MainActivity.FRAGMENT_ROUNDS);
			return roundsFragment;
		}

		return null;

	}
	

	@Override
	public int getCount() {
		return PAGES;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case MainActivity.FRAGMENT_PAYOUT:
			return mContext.getResources().getString(R.string.txt_viewpager_payout_fragment);
		case MainActivity.FRAGMENT_POOL:
			return mContext.getResources().getString(R.string.txt_viewpager_pool_fragment);
		case MainActivity.FRAGMENT_WORKER:
			return mContext.getResources().getString(R.string.txt_viewpager_worker_fragment);
		case MainActivity.FRAGMENT_ROUNDS:
			return mContext.getResources().getString(R.string.txt_viewpager_round_fragment);
		}
		return null;
	}

}
