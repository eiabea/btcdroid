package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;

public class MainViewAdapter extends FragmentStatePagerAdapter {

    public static final String TAG = MainViewAdapter.class.getSimpleName();

    private static int PAGES = 0;

    private Context context;

    private static int[] fragmentOrder;

    public MainViewAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        String userOrder = pref.getString("userOrder", "0:1");
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
            case MainActivity.FRAGMENT_POOL:
                frag = PoolFragment.create();
                break;
            case MainActivity.FRAGMENT_WORKER:
                frag = WorkerFragment.create();
                break;
        }

//        frag = WorkerFragment.create();

        return frag;

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
        switch (which) {
            case MainActivity.FRAGMENT_POOL:
                return context.getResources().getString(R.string.txt_viewpager_pool_fragment);
            case MainActivity.FRAGMENT_WORKER:
                return context.getResources().getString(R.string.txt_viewpager_worker_fragment);
        }

        return "";
    }

}
