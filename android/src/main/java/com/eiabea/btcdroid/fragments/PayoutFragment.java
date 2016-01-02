package com.eiabea.btcdroid.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.TimeTillPayout;
import com.eiabea.btcdroid.util.App;

import java.util.Calendar;
import java.util.Date;

public class PayoutFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = PayoutFragment.class.getSimpleName();

    private static final int PAYOUT_PROFILE_LOADER_ID = 111;
    private static final int PAYOUT_PRICE_LOADER_ID = 112;
    private static final int PAYOUT_TIME_TILL_PAYOUT_LOADER_ID = 113;
    private static final int PAYOUT_STATS_LOADER_ID = 114;

    private TextView txtCurrentSource, txtCurrentValue, txtEstimatedReward, txtConfirmedReward,
            txtTotalReward, txtSendThreshold, txtTimeTillPayout;
    private ProgressBar prgGauge;

    private SwipeRefreshLayout swipeLayout;

    private SharedPreferences pref;
    private LinearLayout llPriceHolder;

    private GenericPrice currentPrice;
    private Profile currentProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(PAYOUT_PROFILE_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(PAYOUT_PRICE_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(PAYOUT_TIME_TILL_PAYOUT_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(PAYOUT_STATS_LOADER_ID, null, this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_payout, root, false);

        Log.i(TAG, "onCreateView()");

        initUi(rootView);

        setListeners();

        return rootView;

    }

    private void initUi(ViewGroup rootView) {
        llPriceHolder = (LinearLayout) rootView.findViewById(R.id.ll_main_info_price_holder);
        txtCurrentSource = (TextView) rootView.findViewById(R.id.txt_main_info_current_source);
        txtCurrentValue = (TextView) rootView.findViewById(R.id.txt_main_info_current_value);

        txtEstimatedReward = (TextView) rootView.findViewById(R.id.txt_main_info_estimated_reward);
        txtConfirmedReward = (TextView) rootView.findViewById(R.id.txt_main_info_confirmed_reward);
        txtTotalReward = (TextView) rootView.findViewById(R.id.txt_main_info_total_reward);

        prgGauge = (ProgressBar) rootView.findViewById(R.id.prg_data_gauge);
        txtSendThreshold = (TextView) rootView.findViewById(R.id.txt_main_info_send_threshold);

        txtTimeTillPayout = (TextView) rootView.findViewById(R.id.txt_main_info_time_till_payout);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.bd_actionbar_background, R.color.bd_black);

    }

    private void setListeners() {
        swipeLayout.setOnRefreshListener(this);
    }

    public static PayoutFragment create() {
        PayoutFragment fragment = new PayoutFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    private void setProfile(final Profile profile) {

        this.currentProfile = profile;

        setGauge(profile);

        fillUpTextviews();

        handleLoading();

    }

    private void fillUpTextviews() {

        int style = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("btc_style_preference", "0"));

        if (this.currentProfile != null) {

            float sendThreshold = Float.valueOf(this.currentProfile.getSend_threshold());
            float estimated = Float.valueOf(this.currentProfile.getEstimated_reward());
            float confirmed = Float.valueOf(this.currentProfile.getConfirmed_reward());
            float unconfirmed = Float.valueOf(this.currentProfile.getUnconfirmed_reward());
            if (style == 3 && this.currentPrice != null) {

                sendThreshold = sendThreshold * this.currentPrice.getValueFloat();
                estimated = estimated * this.currentPrice.getValueFloat();
                confirmed = confirmed * this.currentPrice.getValueFloat();
                unconfirmed = unconfirmed * this.currentPrice.getValueFloat();

                txtEstimatedReward.setText(App.formatPrice(this.currentPrice.getSymbol(), estimated));
                txtConfirmedReward.setText(App.formatPrice(this.currentPrice.getSymbol(), confirmed));
                txtTotalReward.setText(App.formatPrice(this.currentPrice.getSymbol(), confirmed + unconfirmed));

                txtSendThreshold.setText(App.formatPrice(this.currentPrice.getSymbol(), sendThreshold));
            } else {

                txtEstimatedReward.setText(App.formatReward(estimated));
                txtConfirmedReward.setText(App.formatReward(confirmed));
                txtTotalReward.setText(App.formatReward(confirmed + unconfirmed));

                txtSendThreshold.setText(App.formatReward(sendThreshold));
            }
        }


    }

    private void setPrices(GenericPrice price) {
        this.currentPrice = price;

        setPrice(txtCurrentValue, price);

        fillUpTextviews();

        if (llPriceHolder != null) {
            llPriceHolder.setVisibility(App.isPriceEnabled ? View.VISIBLE : View.GONE);
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

//            Log.d(getClass().getSimpleName(), "Pricethreshold min: " + minuteThreshold);
//            Log.d(getClass().getSimpleName(), "Pricethreshold: " + threshold);
//            Log.d(getClass().getSimpleName(), "Price last Updated: " + lastUpdated);
//            Log.d(getClass().getSimpleName(), "Price now: " + now);
//            Log.d(getClass().getSimpleName(), "time until priceupdate: " + (((lastUpdated + threshold) - now) / 1000) + " sec");

            if ((lastUpdated + threshold) < now) {

                Log.d(getClass().getSimpleName(), "threshold expired --> set colors for " + "txt_" + txt.getId());
                if (lastPriceFloat > currentPriceFloat) {
                    txtCurrentValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.bd_red));
                } else {
                    txtCurrentValue.setTextColor(ContextCompat.getColor(getActivity(), R.color.bd_green));
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

    private void setGauge(Profile profile) {
        try {
            int offset = 48;
            int max = 1000;

            float sendThreshold = Float.valueOf(profile.getSend_threshold());
            float confirmed = Float.valueOf(profile.getConfirmed_reward());
            float unconfirmed = Float.valueOf(profile.getUnconfirmed_reward());

            // Gauge only
            int confirmedProgress = (int) ((confirmed / sendThreshold) * (max - (2 * offset)));
            int unconfirmedProgress = (int) ((unconfirmed / sendThreshold) * (max - (2 * offset)));
            int total = unconfirmedProgress + confirmedProgress;

            confirmedProgress += offset;
            total += offset;

            prgGauge.setProgress(0);
            prgGauge.setSecondaryProgress(0);

            ProgressBarAnimation anim = new ProgressBarAnimation(prgGauge, offset, total, -50, confirmedProgress);
            anim.setDuration(1000);
            prgGauge.startAnimation(anim);

        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "Can't set Gauge (NullPointer)");
        }
    }

    private void handleLoading() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
    }

    public class ProgressBarAnimation extends Animation {
        private final ProgressBar progressBar;
        private final float from;
        private final float to;
        private final float secondFrom;
        private final float secondTo;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to, float secondFrom, float secondTo) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
            this.secondFrom = secondFrom;
            this.secondTo = secondTo;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
            float secondValue = secondFrom + (secondTo - secondFrom) * interpolatedTime;
            progressBar.setProgress((int) value);
            progressBar.setSecondaryProgress((int) secondValue);
        }

    }

    @Override
    public void onRefresh() {
        if (App.getInstance().isTokenSet()) {
//            App.resetUpdateManager(getActivity());
            App.updateData(getActivity());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {


        String selection;
        String[] selectionArgs = {"1"};
        switch (which) {
            case PAYOUT_PROFILE_LOADER_ID:
                selection = Profile._ID + "=?";
                return new CursorLoader(getActivity(), Profile.CONTENT_URI, null, selection, selectionArgs, null);
            case PAYOUT_PRICE_LOADER_ID:
                selection = GenericPrice._ID + "=?";
                return new CursorLoader(getActivity(), GenericPrice.CONTENT_URI, null, selection, selectionArgs, null);
            case PAYOUT_TIME_TILL_PAYOUT_LOADER_ID:
                selection = TimeTillPayout._ID + "=?";
                return new CursorLoader(getActivity(), TimeTillPayout.CONTENT_URI, null, selection, selectionArgs, null);
            case PAYOUT_STATS_LOADER_ID:
                selection = Stats._ID + "=?";
                return new CursorLoader(getActivity(), Stats.CONTENT_URI, null, selection, selectionArgs, null);
        }

        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0 && isAdded()) {
            c.moveToFirst();
            TimeTillPayout ttp;
            switch (loader.getId()) {
                case PAYOUT_PROFILE_LOADER_ID:
                    Profile profile = new Profile(c);
                    profile = App.getInstance().gson.fromJson(profile.getJson(), Profile.class);

                    if (profile != null) {
                        setProfile(profile);
                    }
                    break;
                case PAYOUT_PRICE_LOADER_ID:
                    GenericPrice price = new GenericPrice(c);
                    price = App.getInstance().gson.fromJson(price.getJson(), GenericPrice.class);

                    if (price != null) {
                        setPrices(price);
                    }
                    break;
                case PAYOUT_TIME_TILL_PAYOUT_LOADER_ID:
                    ttp = new TimeTillPayout(c);
                    Log.i(TAG, "Remaining Reward: " + ttp.getRemainingReward());
                    Log.i(TAG, "Time: " + ttp.getAverageTime());
                    Log.i(TAG, "Avg per Block: " + ttp.getAvgBtcPerBlock());
                    Log.i(TAG, "TTP: " + ttp.getTimeTillPayout());

                    long ttpMs = ttp.getTimeTillPayout() / 1000;

                    if (ttpMs > 365 * 24 * 60 * 60) {
                        Log.i(TAG, "Over a year");
                        long years = ttpMs / (365 * 24 * 60 * 60);
                        Log.i(TAG, "Years: " + years);
                        txtTimeTillPayout.setText(years + " years");
                        break;
                    }

                    if (ttpMs > 24 * 60 * 60) {
                        Log.i(TAG, "Over a day");
                        long days = ttpMs / (24 * 60 * 60);
                        Log.i(TAG, "Days: " + days);
                        txtTimeTillPayout.setText(days + " days");
                        break;
                    }

                    Log.i(TAG, "Normal: " + App.dateDurationFormat.format(new Date(ttpMs * 1000)));
                    txtTimeTillPayout.setText(App.dateDurationFormat.format(new Date(ttpMs * 1000)));

                    break;
                case PAYOUT_STATS_LOADER_ID:

                    Stats stats = new Stats(c);
                    stats = App.getInstance().gson.fromJson(stats.getJson(), Stats.class);

                    if (stats != null) {
                        ttp = new TimeTillPayout(stats, getContext());
                        DataProvider.insertOrUpdateTimeTillPayout(getContext(), ttp);
                    }

                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

}
