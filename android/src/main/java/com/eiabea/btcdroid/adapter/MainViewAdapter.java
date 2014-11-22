package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.fragments.PayoutFragment;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.RoundsFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;
import com.eiabea.btcdroid.model.AvgLuck;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class MainViewAdapter extends FragmentStatePagerAdapter {

    private static int PAGES = 0;

    private Profile profile;
    private Stats stats;
    private GenericPrice price;
    private AvgLuck avgLuck;

    private Context context;

    private static int[] fragmentOrder;

    public MainViewAdapter(Context context, FragmentManager fm, Profile profile, Stats stats, GenericPrice price, AvgLuck avgLuck) {
        super(fm);

        this.context = context;
        this.profile = profile;
        this.stats = stats;
        this.price = price;
        this.avgLuck = avgLuck;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String userOrder = pref.getString("userOrder", "0:1:2:3");
        String[] split = userOrder.split(":");
        fragmentOrder = new int[split.length];

        for (int i = 0; i < split.length; i++) {
            fragmentOrder[i] = Integer.valueOf(split[i]);
        }

        PAGES = getValidPages(fragmentOrder);
    }

    private int getValidPages(int[] fragmentOrder) {
        int count = 0;
        for (int i : fragmentOrder) {
            if (i != -1) {
                count++;
            }
        }
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        position = positionToCustomPosition(position);

        switch (position) {
            case MainActivity.FRAGMENT_PAYOUT:
                PayoutFragment payoutFragment = PayoutFragment.create(price, profile);
                return payoutFragment;
            case MainActivity.FRAGMENT_POOL:
                PoolFragment poolFragment = PoolFragment.create(profile, stats, avgLuck);
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

    private int positionToCustomPosition(int position) {
        return fragmentOrder[position];
    }

    @Override
    public int getCount() {
        return PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        position = positionToCustomPosition(position);

        return getNameOfFragment(position, context);

    }

    public static String getNameOfFragment(int which, Context context) {
        if (which == MainActivity.FRAGMENT_PAYOUT) {
            return context.getResources().getString(R.string.txt_viewpager_payout_fragment);
        } else if (which == MainActivity.FRAGMENT_POOL) {
            return context.getResources().getString(R.string.txt_viewpager_pool_fragment);
        } else if (which == MainActivity.FRAGMENT_WORKER) {
            return context.getResources().getString(R.string.txt_viewpager_worker_fragment);
        } else if (which == MainActivity.FRAGMENT_ROUNDS) {
            return context.getResources().getString(R.string.txt_viewpager_round_fragment);
        }
        return "";
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

    public void setAvgLuck(AvgLuck avgLuck) {
        this.avgLuck = avgLuck;
    }

}
