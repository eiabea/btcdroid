package com.eiabea.btcdroid.fragments;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class PoolFragment extends Fragment {

	public static final String PARAM_PROFILE = "param_profile";
	public static final String PARAM_STATS = "param_stats";

	private ViewGroup rootView;

	private Profile profile;
	private Stats stats;

	private SharedPreferences pref;

	private TextView txtTotalHashrate, txtAverageHashrate, txtRoundStarted,
			txtRoundDuration, txtEstimatedDuration, txtAverageDuration,
			txtLuck24h, txtLuck7d, txtLuck30d;
	private RatingBar ratRating;

	public static PoolFragment create(Profile profile, Stats stats) {
		PoolFragment fragment = new PoolFragment();
		Bundle b = new Bundle();
		b.putParcelable(PARAM_PROFILE, profile);
		b.putParcelable(PARAM_STATS, stats);
		fragment.setArguments(b);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pool, null);

		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

		this.profile = getArguments().getParcelable(PARAM_PROFILE);
		this.stats = getArguments().getParcelable(PARAM_STATS);

		initUi();

		if (profile != null) {
			setProfile(profile);
		}

		if (stats != null) {
			setStats(stats);
		}

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

			// if ((lastUpdated + threshold) < now) {

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
			// }

			txt.setText(App.formatProcent(current));
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

		// Dirty hack to set ratingbar
		final float ratingToSet = (float) (stars - rating);

		ratRating.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				Log.d(getClass().getSimpleName(), "Rating changed: " + arg1);
				arg0.setRating(ratingToSet);
			}
		});

		ratRating.setRating(ratingToSet);
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

	private void fillUpProfile() {

		ArrayList<Worker> list = profile.getWorkersList();

		int totalHashrate = 0;

		for (Worker tmp : list) {
			totalHashrate += tmp.getHashrate();
		}

		txtTotalHashrate.setText(App.formatHashRate(totalHashrate));
		txtAverageHashrate.setText(App.formatHashRate(profile.getHashrate()));

	}

	private void fillUpStats() {

		Date started = null;
		Date average = null;
		Date duration = null;

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

}
