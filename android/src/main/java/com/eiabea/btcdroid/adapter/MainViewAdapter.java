package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
import com.eiabea.btcdroid.model.Worker;

public class MainViewAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = MainViewAdapter.class.getSimpleName();

    private static int PAGES = 0;

    private Context context;

    private static int[] fragmentOrder;

    public MainViewAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;

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

        Fragment frag = null;

        switch (position) {
            case MainActivity.FRAGMENT_PAYOUT:
                frag = PayoutFragment.create();
                break;
            case MainActivity.FRAGMENT_POOL:
                frag = PoolFragment.create();
                break;
            case MainActivity.FRAGMENT_WORKER:
                frag = WorkerFragment.create();
                break;
            case MainActivity.FRAGMENT_ROUNDS:
                frag = RoundsFragment.create();
                break;
        }

        return frag;

    }

    private int positionToCustomPosition(int position) {
        if(position < fragmentOrder.length){
            return fragmentOrder[position];
        }

        return fragmentOrder[0];
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
        switch (which) {
            case MainActivity.FRAGMENT_PAYOUT:
                return context.getResources().getString(R.string.txt_viewpager_payout_fragment);
            case MainActivity.FRAGMENT_POOL:
                return context.getResources().getString(R.string.txt_viewpager_pool_fragment);
            case MainActivity.FRAGMENT_WORKER:
                return context.getResources().getString(R.string.txt_viewpager_worker_fragment);
            case MainActivity.FRAGMENT_ROUNDS:
                return context.getResources().getString(R.string.txt_viewpager_round_fragment);
        }

        return "";
    }

}
