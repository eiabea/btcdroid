package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
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

public class MainViewAdapter extends FragmentStatePagerAdapter {

	private static final int PAGES = 4;
	
	private Profile profile;
	private Stats stats;
	private GenericPrice price;
	
	private Context context;

	public MainViewAdapter(Context context, FragmentManager fm, Profile profile, Stats stats, GenericPrice price) {
		super(fm);
		
		this.context = context;
		this.profile = profile;
		this.stats = stats;
		this.price = price;
	}

	@Override
	public Fragment getItem(int position) {

		Log.i(getClass().getSimpleName(), "getItem: " + position);
		switch (position) {
		case MainActivity.FRAGMENT_PAYOUT:
			PayoutFragment payoutFragment = PayoutFragment.create(price, profile);
			return payoutFragment;
		case MainActivity.FRAGMENT_POOL:
			PoolFragment poolFragment = PoolFragment.create(profile, stats);
			return poolFragment;
		case MainActivity.FRAGMENT_WORKER:
			WorkerFragment workerFragment = WorkerFragment.create(profile);
			return workerFragment;
		case MainActivity.FRAGMENT_ROUNDS:
			RoundsFragment roundsFragment = RoundsFragment.create(stats);
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
			return context.getResources().getString(R.string.txt_viewpager_payout_fragment);
		case MainActivity.FRAGMENT_POOL:
			return context.getResources().getString(R.string.txt_viewpager_pool_fragment);
		case MainActivity.FRAGMENT_WORKER:
			return context.getResources().getString(R.string.txt_viewpager_worker_fragment);
		case MainActivity.FRAGMENT_ROUNDS:
			return context.getResources().getString(R.string.txt_viewpager_round_fragment);
		}
		return null;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public GenericPrice getPrice() {
		return price;
	}

	public void setPrice(GenericPrice price) {
		this.price = price;
	}

}
