package com.eiabea.btcdroid.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Pool;
import com.eiabea.btcdroid.model.User;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

import java.util.Calendar;
import java.util.Date;

public class PoolFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = PoolFragment.class.getSimpleName();

    private static final int POOL_LOADER_ID = 222;
    private static final int USER_LOADER_ID = 223;
    private static final int PRICE_LOADER_ID = 224;

    private ViewGroup rootView;

    private SharedPreferences pref;

    private SwipeRefreshLayout swipeLayout;

    private TextView txtCurrentSource, txtCurrentValue,txtHashrate, txtTotalReward, txtPaidReward, txtUnpaidReward, txtPoolSpeed, txtDifficulty;

    private LinearLayout llPriceHolder;

    public static PoolFragment create() {
        PoolFragment fragment = new PoolFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(POOL_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(USER_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(PRICE_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");

        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pool, null);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initUi();

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.bd_actionbar_background, R.color.bd_black);

        return rootView;
    }

    private void initUi() {
        llPriceHolder = (LinearLayout) rootView.findViewById(R.id.ll_main_info_price_holder);
        txtCurrentSource = (TextView) rootView.findViewById(R.id.txt_main_info_current_source);
        txtCurrentValue = (TextView) rootView.findViewById(R.id.txt_main_info_current_value);

        txtHashrate = (TextView) rootView.findViewById(R.id.txt_main_info_hashrate);
        txtTotalReward = (TextView) rootView.findViewById(R.id.txt_main_info_total_reward);
        txtPaidReward = (TextView) rootView.findViewById(R.id.txt_main_info_paid_reward);
        txtUnpaidReward = (TextView) rootView.findViewById(R.id.txt_main_info_unpaid_reward);
        txtPoolSpeed = (TextView) rootView.findViewById(R.id.txt_main_info_pool_speed);
        txtDifficulty = (TextView) rootView.findViewById(R.id.txt_main_info_difficulty);
    }

    private void handleLoading() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (App.getInstance().isTokenSet()) {
            App.resetUpdateManager(getActivity());
        }
    }

    public void setPrices(GenericPrice price) {
        setPrice(txtCurrentValue, price);

        if (llPriceHolder != null) {
            if (App.isPriceEnabled) {
                llPriceHolder.setVisibility(View.VISIBLE);

            } else {
                llPriceHolder.setVisibility(View.GONE);

            }
        }

    }

    private void setPrice(TextView txt, GenericPrice current) {
        if (current != null && txt != null) {
            float lastPriceFloat = pref.getFloat("txt_" + txt.getId() + "_value", 0f);
            float currentPriceFloat = current.getValueFloat();

            int minuteThreshold = App.getInstance().getPriceThreshold();
            long threshold = minuteThreshold * 60 * 1000;

            long lastUpdated = pref.getLong("txt_" + txt.getId(), 0);

            long now = Calendar.getInstance().getTimeInMillis();

            if ((lastUpdated + threshold) < now) {

                Log.d(getClass().getSimpleName(), "threshold expired --> set colors for " + "txt_" + txt.getId());
                if (lastPriceFloat > currentPriceFloat) {
                    txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_red));
                } else {
                    txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_green));
                }

                pref.edit().putFloat("txt_" + txt.getId() + "_value", currentPriceFloat).apply();
                pref.edit().putLong("txt_" + txt.getId(), Calendar.getInstance().getTimeInMillis()).apply();
//                Log.d(getClass().getSimpleName(), "set last price to: " + currentPriceFloat);
            }

            txtCurrentValue.setText(App.formatPrice(current.getSymbol(), current.getValueFloat()));
            txtCurrentSource.setText(current.getSource() + ":");

            handleLoading();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {

        String selection = Pool._ID + "=?";
        String[] selectionArgs = {"1"};

        switch (which) {
            case POOL_LOADER_ID:
                return new CursorLoader(getActivity(), Pool.CONTENT_URI, null, selection, selectionArgs, null);
            case USER_LOADER_ID:
                return new CursorLoader(getActivity(), User.CONTENT_URI, null, selection, selectionArgs, null);
            case PRICE_LOADER_ID:
                return new CursorLoader(getActivity(), GenericPrice.CONTENT_URI, null, selection, selectionArgs, null);
        }

        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {

            handleLoading();

            c.moveToFirst();

            switch (loader.getId()) {
                case POOL_LOADER_ID:
                    Pool pool = new Pool(c);

                    txtHashrate.setText(App.formatHashRate(App.getTotalHashrate(getActivity())));
                    txtPoolSpeed.setText(App.formatHashRate(pool.getPool_speed() * 1000));
                    txtDifficulty.setText(String.valueOf(pool.getDifficulty()));
                    break;
                case USER_LOADER_ID:

                    User user = new User(c);

                    txtTotalReward.setText(App.formatReward(user.getTotal_rewards()));
                    txtPaidReward.setText(App.formatReward(user.getPaid_rewards()));
                    txtUnpaidReward.setText(App.formatReward(user.getUnpaid_rewards()));
                    break;
                case PRICE_LOADER_ID:
                    GenericPrice price = new GenericPrice(c);
                    price = App.getInstance().gson.fromJson(price.getJson(), GenericPrice.class);

                    if (price != null) {
                        setPrices(price);
                    }
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
