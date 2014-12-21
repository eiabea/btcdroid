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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.AvgLuck;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PoolFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = PoolFragment.class.getSimpleName();

    public static final String PARAM_PROFILE = "param_profile";
    public static final String PARAM_STATS = "param_stats";
    public static final String PARAM_AVG_LUCK = "param_avg_luck";
    private static final int POOL_PROFILE_LOADER_ID = 222;

    private ViewGroup rootView;

    private Stats stats;
    private AvgLuck avgLuck;

    private boolean statsLoaded = false;
    private boolean profileLoaded = false;
    private boolean avgLuckLoaded = false;

    private SharedPreferences pref;

    private SwipeRefreshLayout swipeLayout;

    private TextView txtTotalHashrate, txtAverageHashrate, txtRoundStarted,
            txtRoundDuration, txtEstimatedDuration, txtAverageDuration,
            txtLuck24h, txtLuck7d, txtLuck30d, txtAvgLuck;
    private RatingBar ratRating;

    public static PoolFragment create(Stats stats, AvgLuck avgLuck) {
        PoolFragment fragment = new PoolFragment();
        Bundle b = new Bundle();
        b.putParcelable(PARAM_STATS, stats);
        b.putParcelable(PARAM_AVG_LUCK, avgLuck);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView()");

        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pool, null);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        this.stats = getArguments().getParcelable(PARAM_STATS);
        this.avgLuck = getArguments().getParcelable(PARAM_AVG_LUCK);

        initUi();

//        setStats(stats);
//
//        setAvgLuck(avgLuck);
//
//        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
//        swipeLayout.setOnRefreshListener(this);
//        swipeLayout.setColorSchemeResources(R.color.bd_actionbar_background, R.color.bd_black);

        getActivity().getSupportLoaderManager().initLoader(POOL_PROFILE_LOADER_ID, null, this);


        return rootView;
    }

    private void initUi() {
        txtTotalHashrate = (TextView) rootView.findViewById(R.id.txt_main_info_total_hashrate);
        txtAverageHashrate = (TextView) rootView.findViewById(R.id.txt_main_info_average_hashrate);
        txtRoundStarted = (TextView) rootView.findViewById(R.id.txt_main_info_round_started);
        txtRoundDuration = (TextView) rootView.findViewById(R.id.txt_main_info_round_duration);
        txtEstimatedDuration = (TextView) rootView.findViewById(R.id.txt_main_info_estimated_duration);
        txtAverageDuration = (TextView) rootView.findViewById(R.id.txt_main_info_average_duration);
        ratRating = (RatingBar) rootView.findViewById(R.id.rat_main_info_rating);
        txtLuck24h = (TextView) rootView.findViewById(R.id.txt_main_info_luck_24h);
        txtLuck7d = (TextView) rootView.findViewById(R.id.txt_main_info_luck_7d);
        txtLuck30d = (TextView) rootView.findViewById(R.id.txt_main_info_luck_30d);
        txtAvgLuck = (TextView) rootView.findViewById(R.id.txt_main_info_avg_luck);
    }

    private void setLuck(TextView txt, float current, boolean highPrecision) {
        if (current > 0) {
            float last = pref.getFloat("txt_" + txt.getId() + "_value", 0f);

            int minuteThreshold = App.getInstance().getLuckThreshold();
            long threshold = minuteThreshold * 60 * 1000;

            long lastUpdated = pref.getLong("txt_" + txt.getId(), 0);

            long now = Calendar.getInstance().getTimeInMillis();

            if ((lastUpdated + threshold) < now) {

                if (last > current) {
                    txt.setTextColor(getResources().getColor(R.color.bd_red));
                } else if (last < current) {
                    txt.setTextColor(getResources().getColor(R.color.bd_green));
                } else {
                    txt.setTextColor(getResources().getColor(R.color.bd_dark_grey_text));
                }
                pref.edit().putFloat("txt_" + txt.getId() + "_value", current).apply();
                pref.edit().putLong("txt_" + txt.getId(), Calendar.getInstance().getTimeInMillis()).apply();
            }

            if (highPrecision) {
                txt.setText(App.formatProcentHighPrecision(current));
            } else {
                txt.setText(App.formatProcent(current));
            }
        } else {
            txt.setText(App.formatProcent(current));
        }
    }

    private void setRatingBar(double rating) {
        double stars = ratRating.getNumStars();

        if (rating < 0) {
            rating = 0;
        }

        if (rating > stars) {
            rating = stars;
        }

        final float ratingToSet = (float) (stars - rating);
        // final float ratingToSet = 2.4f;

        // Dirty hack to set ratingbar
        ratRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            @Override
            public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
                Log.d(TAG, "Rating changed: " + arg1);
                arg0.setRating(ratingToSet);
            }
        });

        ratRating.setStepSize(0.5f);

        ratRating.setRating(ratingToSet);
        Log.d(TAG, "Rating set: " + ratRating.getRating());

    }

    private Date getAverageRoundTime(List<Block> blocks) {

        long total = 0;

        for (Block tmpBlock : blocks) {

            Date duration;
            try {
                duration = App.dateDurationFormat.parse(tmpBlock.getMining_duration());
                total += duration.getTime();
            } catch (ParseException e) {
                Log.e(TAG, "Can't get AverageRoundTime (NullPointer)");
            }

        }

        long average = total / blocks.size();

        Log.d(TAG, "Total: " + total + "; " + "Avg.: " + average);

        return new Date(average);
    }

    private double calculateRoundRating(Date average, Date duration) {

        double avg = average.getTime();
        double dur = duration.getTime();

        double rating = dur / avg;

        Log.d(TAG, "Raw Rating: " + rating);

        return rating;
    }

    private void setProfile(Profile profile) {
        try {
            txtTotalHashrate.setText(App.formatHashRate(App.getTotalHashrate(profile)));
            txtAverageHashrate.setText(App.formatHashRate(profile.getHashrate()));
        } catch (NullPointerException ignore) {
        }
        profileLoaded = true;
        handleLoading();
    }

    public void setStats(Stats stats) {
        this.stats = stats;
        fillUpStats();
        statsLoaded = true;
        handleLoading();
    }

    public void setAvgLuck(AvgLuck avgLuck) {
        this.avgLuck = avgLuck;
        fillUpAvgLuck();
        avgLuckLoaded = true;
        handleLoading();

    }

    private void handleLoading() {
        if (swipeLayout != null &&
                avgLuckLoaded &&
                profileLoaded &&
                statsLoaded) {
            swipeLayout.setRefreshing(false);
            avgLuckLoaded = profileLoaded = statsLoaded = false;
        }
    }

    private void fillUpStats() {

        Date started;
        Date average;
        Date duration = null;

        try {
            average = getAverageRoundTime(App.parseBlocks(stats.getBlocks()));
            txtAverageDuration.setText(App.dateDurationFormat.format(average));
            try {
                started = App.dateStatsFormat.parse(stats.getRound_started());
                txtRoundStarted.setText(App.dateFormat.format(started));
            } catch (java.text.ParseException e) {
                Log.e(TAG, "Can't get RoundStarted (NullPointer)");
            }
            try {
                duration = App.dateDurationFormat.parse(stats.getRound_duration());
                txtRoundDuration.setText(App.dateDurationFormat.format(duration));
            } catch (java.text.ParseException e) {
                txtRoundDuration.setText(getString(R.string.txt_greater_one_day));
                Log.e(TAG, "Can't get RoundDuration (NullPointer)");
            }

            if (average != null && duration != null) {
                double rating = calculateRoundRating(average, duration);

                setRatingBar(rating);
            }

            if (duration != null) {
                float cdf = Float.valueOf(stats.getShares_cdf());
                Log.i(TAG, "cdf: " + cdf);
                float estimated = (duration.getTime() / (cdf / 100));
                Log.i(TAG, "estimated: " + (long) estimated);
                Log.i(TAG, "duration: " + duration.getTime());

                txtEstimatedDuration.setText(App.dateDurationFormat.format(new Date((long) (estimated/* + duration.getTime()*/))));
            } else {
                txtEstimatedDuration.setText(getString(R.string.txt_infinite_symbol));
            }

            float currentLuck24 = Float.parseFloat(stats.getLuck_1());
            float currentLuck7d = Float.parseFloat(stats.getLuck_7());
            float currentLuck30d = Float.parseFloat(stats.getLuck_30());

            setLuck(txtLuck24h, currentLuck24, false);
            setLuck(txtLuck7d, currentLuck7d, false);
            setLuck(txtLuck30d, currentLuck30d, false);
        } catch (NullPointerException ignore) {
        }

    }

    private void fillUpAvgLuck() {
        if (avgLuck != null) {
            setLuck(txtAvgLuck, avgLuck.getAvg_luck(), true);
        }
    }

    @Override
    public void onRefresh() {
        if (App.getInstance().isTokenSet()) {
            App.resetUpdateManager(getActivity());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {


        String selection = Profile._ID + "=?";
        String[] selectionArgs = {"1"};

        return new CursorLoader(getActivity(), Profile.CONTENT_URI, null, selection, selectionArgs, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {
            switch (loader.getId()) {
                case POOL_PROFILE_LOADER_ID:
                    c.moveToFirst();

                    Profile profile = new Profile(c);
                    profile = App.getInstance().gson.fromJson(profile.getJson(), Profile.class);

                    if (profile != null) {
                        setProfile(profile);
                    }
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
