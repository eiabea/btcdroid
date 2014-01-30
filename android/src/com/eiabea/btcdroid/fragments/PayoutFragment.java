package com.eiabea.btcdroid.fragments;

import java.util.Calendar;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.util.App;

public class PayoutFragment extends Fragment {

	public static final String PARAM_PRICE = "param_price";
	public static final String PARAM_PROFILE = "param_profile";

	private GenericPrice price;
	private Profile profile;

	private View rootView;
	private TextView txtCurrentValue, txtEstimatedReward, txtConfirmedReward,
			txtTotalReward, txtSendThreshold;
	private ProgressBar prgGauge;

	private SharedPreferences pref;
	private LinearLayout llPriceHolder;

	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		this.rootView = inflater.inflate(R.layout.fragment_payout, root, false);

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		price = getArguments().getParcelable(PARAM_PRICE);
		profile = getArguments().getParcelable(PARAM_PROFILE);

		initUi();

		setListeners();

		if (price != null) {
			setPrices(price);
		}

		if (profile != null) {
			setProfile(profile);
		}

		return rootView;

	}

	private void initUi() {
		llPriceHolder = (LinearLayout) rootView.findViewById(R.id.ll_main_info_price_holder);
		txtCurrentValue = (TextView) rootView.findViewById(R.id.txt_main_info_current_value);

		txtEstimatedReward = (TextView) rootView.findViewById(R.id.txt_main_info_estimated_reward);
		txtConfirmedReward = (TextView) rootView.findViewById(R.id.txt_main_info_confirmed_reward);
		txtTotalReward = (TextView) rootView.findViewById(R.id.txt_main_info_total_reward);

		prgGauge = (ProgressBar) rootView.findViewById(R.id.prg_data_gauge);
		txtSendThreshold = (TextView) rootView.findViewById(R.id.txt_main_info_send_threshold);
	}

	private void setListeners() {
	}

	public static PayoutFragment create(GenericPrice price, Profile profile) {
		PayoutFragment fragment = new PayoutFragment();
		Bundle b = new Bundle();
		b.putParcelable(PARAM_PRICE, price);
		b.putParcelable(PARAM_PROFILE, profile);
		fragment.setArguments(b);
		return fragment;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;

		setGauge(profile);

	}

	public void setPrices(GenericPrice price) {
		this.price = price;
		if (txtCurrentValue != null && this.price != null) {
			fillUpPrices();
		}

		if (llPriceHolder != null) {
			if (App.isPriceEnabled) {
				llPriceHolder.setVisibility(View.VISIBLE);

			} else {
				llPriceHolder.setVisibility(View.GONE);

			}
		}

	}

	private void setPrice(TextView txt, GenericPrice current) {
		if (current != null) {
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

				pref.edit().putFloat("txt_" + txt.getId() + "_value", currentPriceFloat).commit();
				pref.edit().putLong("txt_" + txt.getId(), Calendar.getInstance().getTimeInMillis()).commit();
				Log.d(getClass().getSimpleName(), "set last price to: " + currentPriceFloat);
			}

			txtCurrentValue.setText(App.formatPrice(current.getValueFloat()));
		}
	}

	private void setGauge(Profile profile) {
		try {
			int max = 100;
			int offset = 4;

			float sendThreshold = Float.valueOf(profile.getSend_threshold());
			float estimated = Float.valueOf(profile.getEstimated_reward());
			float confirmed = Float.valueOf(profile.getConfirmed_reward());
			float unconfirmed = Float.valueOf(profile.getUnconfirmed_reward());

			txtEstimatedReward.setText(App.formatReward(estimated));
			txtConfirmedReward.setText(App.formatReward(confirmed));
			txtTotalReward.setText(App.formatReward(confirmed + unconfirmed));
			txtSendThreshold.setText(App.formatReward(sendThreshold));

			// Gauge only
			int confirmedProgress = (int) ((confirmed / sendThreshold) * max);
			int unconfirmedProgress = (int) ((unconfirmed / sendThreshold) * max);
			int total = unconfirmedProgress + confirmedProgress;

			confirmedProgress += offset;
			total += offset;

			if (confirmedProgress > max) {
				confirmedProgress = max + offset;
			}

			if (total > max) {
				total = max + offset;
			}

			prgGauge.setMax(max + (2 * offset));

			ProgressBarAnimation anim = new ProgressBarAnimation(prgGauge, offset, total, -50, confirmedProgress);
			anim.setDuration(1000);
			prgGauge.startAnimation(anim);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void fillUpPrices() {

		setPrice(txtCurrentValue, price);

	}

	public class TotalAnimator extends AsyncTask<Integer, Void, Void> {

		@Override
		protected void onProgressUpdate(Void... values) {
			prgGauge.incrementProgressBy(1);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			while (prgGauge.getProgress() < params[0]) {
				publishProgress();
				try {
					Thread.sleep(10);
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
			prgGauge.incrementSecondaryProgressBy(1);
		}

		@Override
		protected Void doInBackground(Integer... params) {
			while (prgGauge.getSecondaryProgress() < params[0]) {
				publishProgress();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
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

}
