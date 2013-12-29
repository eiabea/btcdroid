package com.eiabea.btcdroid.fragments;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class PoolFragment extends Fragment {

	private ViewGroup rootView;

	private Profile profile;
	private Stats stats;
	private GenericPrice price;

	private SharedPreferences pref;

	private LinearLayout llPriceHolder;

	private TextView txtEstimatedReward, txtConfirmedReward, txtTotalReward,
			txtCurrentValue, txtTotalHashrate, txtAverageHashrate,
			txtRoundStarted, txtRoundDuration, txtEstimatedDuration,
			txtAverageDuration, txtLuck24h, txtLuck7d, txtLuck30d;
	private RatingBar ratRating;

	public static PoolFragment create(int pageNumber) {
		PoolFragment fragment = new PoolFragment();
		Bundle b = new Bundle();
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");
		setRetainInstance(true);
		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pool, null);

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		initUi(inflater, rootView);

		if (profile != null) {
			setProfile(profile);
		}

		if (stats != null) {
			setStats(stats);
		}

		if (price != null) {
			setPrices(price);
		}

		return rootView;
	}

	@SuppressLint("NewApi")
	private void initUi(LayoutInflater inflater, ViewGroup rootView) {
		llPriceHolder = (LinearLayout) rootView.findViewById(R.id.ll_main_info_price_holder);
		txtCurrentValue = (TextView) rootView.findViewById(R.id.txt_main_info_current_value);
		txtEstimatedReward = (TextView) rootView.findViewById(R.id.txt_main_info_estimated_reward);
		txtConfirmedReward = (TextView) rootView.findViewById(R.id.txt_main_info_confirmed_reward);
		txtTotalReward = (TextView) rootView.findViewById(R.id.txt_main_info_total_reward);
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
	}

	private void setLuck(TextView txt, float current) {
		if (current > 0) {

			float last = pref.getFloat("txt_" + txt.getId() + "_value", 0f);

			int minuteThreshold = App.getInstance().getThreshold();
			long threshold = minuteThreshold * 60 * 1000;

			long lastUpdated = pref.getLong("txt_" + txt.getId(), 0);

			long now = Calendar.getInstance().getTimeInMillis();

			Log.d(getClass().getSimpleName(), "threshold min: " + minuteThreshold);
			Log.d(getClass().getSimpleName(), "threshold: " + threshold);
			Log.d(getClass().getSimpleName(), "last Updated: " + lastUpdated);
			Log.d(getClass().getSimpleName(), "now: " + now);
			Log.d(getClass().getSimpleName(), "time until update: " + (((lastUpdated + threshold) - now) / 1000) + " sec");

			if ((lastUpdated + threshold) < now) {

				Log.d(getClass().getSimpleName(), "threshold expired --> set colors for " + "txt_" + txt.getId());
				if (last > current) {
					txt.setTextColor(getResources().getColor(R.color.bd_red));
				} else if (last < current) {
					txt.setTextColor(getResources().getColor(R.color.bd_green));
				} else {
					txt.setTextColor(getResources().getColor(R.color.bd_dark_grey_text));
				}
				pref.edit().putFloat("txt_" + txt.getId() + "_value", current).commit();
				pref.edit().putLong("txt_" + txt.getId(), Calendar.getInstance().getTimeInMillis()).commit();
				Log.d(getClass().getSimpleName(), "set last luck to: " + current);
			}

			txt.setText(App.formatProcent(current));
		} else {
			txt.setText(App.formatProcent(current));
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

	private void setRatingBar(double rating) {
		double stars = ratRating.getNumStars();

		if (rating < 0) {
			rating = 0;
		}

		if (rating > stars) {
			rating = stars;
		}

		ratRating.setRating((float) (stars - rating));
		Log.d(getClass().getSimpleName(), "Rating set: " + ratRating.getRating());

	}

	private Date getAverageRoundTime(List<Block> blocks) {

		long total = 0;

		for (Block tmpBlock : blocks) {

			Date duration;
			try {
				duration = App.dateDurationFormat.parse(tmpBlock.getMining_duration());
				total += duration.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		long average = total / blocks.size();

		Log.d(getClass().getSimpleName(), "Total: " + total + "; " + "Avg.: " + average);

		return new Date(average);
	}

	private double calculateRoundRating(Date average, Date duration) {

		double avg = average.getTime();
		double dur = duration.getTime();

		double rating = dur / avg;

		Log.d(getClass().getSimpleName(), "Raw Rating: " + rating);

		return rating;
	}

	public void setProfile(Profile profile) {

		this.profile = profile;
		if (txtTotalHashrate != null && this.profile != null) {
			fillUpProfile();
		}

	}

	public void setStats(Stats stats) {

		this.stats = stats;
		if (txtRoundDuration != null && this.stats != null) {
			fillUpStats();
		}

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
	
	private void fillUpProfile() {

		ArrayList<Worker> list = profile.getWorkersList();

		int totalHashrate = 0;

		for (Worker tmp : list) {
			totalHashrate += tmp.getHashrate();
		}

		float estimated = Float.valueOf(profile.getEstimated_reward());
		float unconfirmed = Float.valueOf(profile.getUnconfirmed_reward());
		float confirmed = Float.valueOf(profile.getConfirmed_reward());
		float total = confirmed + unconfirmed;

		txtEstimatedReward.setText(App.formatReward(estimated));
		txtConfirmedReward.setText(App.formatReward(confirmed));
		txtTotalReward.setText(App.formatReward(total));

		txtTotalHashrate.setText(App.formatHashRate(totalHashrate));
		txtAverageHashrate.setText(App.formatHashRate(profile.getHashrate()));

	}

	private void fillUpStats() {

		Date started, average, duration = null;

		average = getAverageRoundTime(App.parseBlocks(stats.getBlocks()));
		txtAverageDuration.setText(App.dateDurationFormat.format(average));
		try {
			started = App.dateStatsFormat.parse(stats.getRound_started());
			txtRoundStarted.setText(App.dateFormat.format(started));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		try {
			duration = App.dateDurationFormat.parse(stats.getRound_duration());
			txtRoundDuration.setText(App.dateDurationFormat.format(duration));
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		if (average != null && duration != null) {
			double rating = calculateRoundRating(average, duration);

			setRatingBar(rating);
		}

		if (duration != null) {
			float cdf = Float.valueOf(stats.getShares_cdf());
			System.out.println(cdf);
			float estimated = (duration.getTime() / (cdf / 100));
			
			txtEstimatedDuration.setText(App.dateDurationFormat.format(new Date((long) estimated)));
		}

		float currentLuck24 = Float.parseFloat(stats.getLuck_1());
		float currentLuck7d = Float.parseFloat(stats.getLuck_7());
		float currentLuck30d = Float.parseFloat(stats.getLuck_30());

		setLuck(txtLuck24h, currentLuck24);
		setLuck(txtLuck7d, currentLuck7d);
		setLuck(txtLuck30d, currentLuck30d);

	}

	private void fillUpPrices() {

		setPrice(txtCurrentValue, price);

	}

}
