package com.eiabea.btcdroid.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

import java.util.Calendar;

public class PayoutFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PAYOUT_PROFILE_LOADER_ID = 111;
    private static final int PAYOUT_PRICE_LOADER_ID = 112;

    private boolean profileLoaded = false;
    private boolean pricesLoaded = false;

    private View rootView;
    private TextView txtCurrentSource, txtCurrentValue, txtEstimatedReward, txtConfirmedReward,
            txtTotalReward, txtSendThreshold;
    private ProgressBar prgGauge;

    private SwipeRefreshLayout swipeLayout;

    private SharedPreferences pref;
    private LinearLayout llPriceHolder;

    public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_payout, root, false);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        initUi();

        setListeners();

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeResources(R.color.bd_actionbar_background, R.color.bd_black);

        getActivity().getSupportLoaderManager().initLoader(PAYOUT_PROFILE_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(PAYOUT_PRICE_LOADER_ID, null, this);

        return rootView;

    }

    private void initUi() {
        llPriceHolder = (LinearLayout) rootView.findViewById(R.id.ll_main_info_price_holder);
        txtCurrentSource = (TextView) rootView.findViewById(R.id.txt_main_info_current_source);
        txtCurrentValue = (TextView) rootView.findViewById(R.id.txt_main_info_current_value);

        txtEstimatedReward = (TextView) rootView.findViewById(R.id.txt_main_info_estimated_reward);
        txtConfirmedReward = (TextView) rootView.findViewById(R.id.txt_main_info_confirmed_reward);
        txtTotalReward = (TextView) rootView.findViewById(R.id.txt_main_info_total_reward);

        prgGauge = (ProgressBar) rootView.findViewById(R.id.prg_data_gauge);
        txtSendThreshold = (TextView) rootView.findViewById(R.id.txt_main_info_send_threshold);
    }

    private void setListeners() {
    }

    public static PayoutFragment create() {
        PayoutFragment fragment = new PayoutFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    private void setProfile(final Profile profile) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            Log.d(getClass().getSimpleName(), "Under Honeycomb");
            Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
            rotate.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    prgGauge.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Log.d(getClass().getSimpleName(), "End rotate animation");
                    prgGauge.setVisibility(View.VISIBLE);
                    setGauge(profile, true);
                }
            });
            prgGauge.setAnimation(rotate);
        } else {
            setGauge(profile, false);
        }

        profileLoaded = true;
        handleLoading();


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

            Log.d(getClass().getSimpleName(), "Pricethreshold min: " + minuteThreshold);
            Log.d(getClass().getSimpleName(), "Pricethreshold: " + threshold);
            Log.d(getClass().getSimpleName(), "Price last Updated: " + lastUpdated);
            Log.d(getClass().getSimpleName(), "Price now: " + now);
            Log.d(getClass().getSimpleName(), "time until priceupdate: " + (((lastUpdated + threshold) - now) / 1000) + " sec");

            if ((lastUpdated + threshold) < now) {

                Log.d(getClass().getSimpleName(), "threshold expired --> set colors for " + "txt_" + txt.getId());
                if (lastPriceFloat > currentPriceFloat) {
                    txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_red));
                } else {
                    txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_green));
                }

                pref.edit().putFloat("txt_" + txt.getId() + "_value", currentPriceFloat).apply();
                pref.edit().putLong("txt_" + txt.getId(), Calendar.getInstance().getTimeInMillis()).apply();
                Log.d(getClass().getSimpleName(), "set last price to: " + currentPriceFloat);
            }

            txtCurrentValue.setText(App.formatPrice(current.getSymbol(), current.getValueFloat()));
            txtCurrentSource.setText(current.getSource() + ":");

            pricesLoaded = true;
            handleLoading();
        }
    }

    private void setGauge(Profile profile, boolean preHoneyComb) {
        try {
            int offset = 48;
            int max = 1000;

            float sendThreshold = Float.valueOf(profile.getSend_threshold());
            float estimated = Float.valueOf(profile.getEstimated_reward());
            float confirmed = Float.valueOf(profile.getConfirmed_reward());
            float unconfirmed = Float.valueOf(profile.getUnconfirmed_reward());

            txtEstimatedReward.setText(App.formatReward(estimated));
            txtConfirmedReward.setText(App.formatReward(confirmed));
            txtTotalReward.setText(App.formatReward(confirmed + unconfirmed));
            txtSendThreshold.setText(App.formatReward(sendThreshold));

            // Gauge only
            int confirmedProgress = (int) ((confirmed / sendThreshold) * (max - (2 * offset)));
            int unconfirmedProgress = (int) ((unconfirmed / sendThreshold) * (max - (2 * offset)));
            int total = unconfirmedProgress + confirmedProgress;

            confirmedProgress += offset;
            total += offset;

            prgGauge.setProgress(0);
            prgGauge.setSecondaryProgress(0);


            if (preHoneyComb) {
                new TotalAnimator().execute(total);
                new ConfirmedAnimator().execute(confirmedProgress);
            } else {
                ProgressBarAnimation anim = new ProgressBarAnimation(prgGauge, offset, total, -50, confirmedProgress);
                anim.setDuration(1000);
                prgGauge.startAnimation(anim);
            }

        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "Can't set Gauge (NullPointer)");
        }
    }

    private void handleLoading() {
        if (swipeLayout != null &&
                pricesLoaded &&
                profileLoaded) {
            swipeLayout.setRefreshing(false);
            pricesLoaded = profileLoaded = false;
        }
    }

    public class ProgressBarAnimation extends Animation {
        private ProgressBar progressBar;
        private float from;
        private float to;
        private float secondFrom;
        private float secondTo;

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

    public class TotalAnimator extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            prgGauge.incrementProgressBy(10);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            while (prgGauge.getProgress() < params[0]) {
                publishProgress();
                try {
                    Thread.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public class ConfirmedAnimator extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onProgressUpdate(Void... values) {
            prgGauge.incrementSecondaryProgressBy(10);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            while (prgGauge.getSecondaryProgress() < params[0]) {
                publishProgress();
                try {
                    Thread.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
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


        String selection;
        String[] selectionArgs = {"1"};
        switch (which) {
            case PAYOUT_PROFILE_LOADER_ID:
                selection = Profile._ID + "=?";
                return new CursorLoader(getActivity(), Profile.CONTENT_URI, null, selection, selectionArgs, null);
            case PAYOUT_PRICE_LOADER_ID:
                selection = GenericPrice._ID + "=?";
                return new CursorLoader(getActivity(), GenericPrice.CONTENT_URI, null, selection, selectionArgs, null);
        }

        return null;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {
            switch (loader.getId()) {
                case PAYOUT_PROFILE_LOADER_ID:
                    c.moveToFirst();

                    Profile profile = new Profile(c);
                    profile = App.getInstance().gson.fromJson(profile.getJson(), Profile.class);

                    if (profile != null) {
                        setProfile(profile);
                    }
                    break;
                case PAYOUT_PRICE_LOADER_ID:
                    c.moveToFirst();

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
