package com.eiabea.btcdroid.fragments;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
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
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

public class PoolFragment extends Fragment {

	private ViewGroup rootView;
	
	private Profile profile;
	private Stats stats;
	private Prices prices;

	private Context context;

	private TextView txtConfirmedReward, txtCurrentValue, txtTotalHashrate,
			txtAverageHashrate, txtRoundStarted, txtRoundDuration,
			txtEstimatedDuration, txtAverageDuration, txtLuck24h, txtLuck7d,
			txtLuck30d;
	private LinearLayout llInfoHolder; // , llWorkerHolder;
	private RatingBar ratRating;

	public static PoolFragment create(int pageNumber) {
		PoolFragment fragment = new PoolFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.i(getClass().getSimpleName(), "onCreateView()");

		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.fragment_pool, null);

		initUi(inflater, rootView);
		
		if(profile != null){
			setProfile(profile);
		}
		
		if(stats != null){
			setStats(stats);
		}
		
		if(prices != null){
			setPrices(prices);
		}

		return rootView;
	}

	private void initUi(LayoutInflater inflater, ViewGroup rootView) {
		txtConfirmedReward = (TextView) rootView.findViewById(R.id.txt_main_info_confirmed_reward);
		txtCurrentValue = (TextView) rootView.findViewById(R.id.txt_main_info_current_value);
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

		llInfoHolder = (LinearLayout) rootView.findViewById(R.id.ll_main_info_holder);
	}

	private void setLuck(TextView txt, float last, float current) {
		if (last > 0 && current > 0) {

			if (last > current) {
				txt.setTextColor(getResources().getColor(R.color.bd_red));
			} else if (last < current) {
				txt.setTextColor(getResources().getColor(R.color.bd_green));
			}

			txt.setText(formatProcent(current));
		} else {
			txt.setText(formatProcent(current));
		}
	}

	private void setPrice(TextView txt, Price last, Price current) {
		if (last != null && current != null) {
			float lastPriceFloat = Float.parseFloat(last.getValue());
			float currentPriceFloat = Float.parseFloat(current.getValue());

			if (lastPriceFloat > currentPriceFloat) {
				txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_red));
			} else if (lastPriceFloat < currentPriceFloat) {
				txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_green));
			}

			txtCurrentValue.setText(current.getDisplay_short());
		} else if (current != null) {
			txtCurrentValue.setText(current.getDisplay_short());
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

		System.out.println("Total: " + total + "; " + "Avg.: " + average);

		return new Date(average);
	}

	private double calculateRoundRating(Date average, Date duration) {

		double avg = average.getTime();
		double dur = duration.getTime();

		double rating = dur / avg;

		System.out.println("Raw Rating: " + rating);

		return rating;
	}

	private String formatProcent(float raw) {
		return String.format("%.0f", raw * 100) + " %";
	}
	
	public void setProfile(Profile profile) {
		
		this.profile = profile;
		if(txtTotalHashrate != null && this.profile != null){
			fillUpProfile();
		}
		
		
	}

	public void setStats(Stats stats) {
		
		this.stats = stats;
		if(txtRoundDuration != null && this.stats != null){
			fillUpStats();
		}
		

	}
	
	public void setPrices(Prices prices) {
		System.out.println("prices");
		this.prices = prices;
		if(txtCurrentValue != null && this.prices != null){
			fillUpPrices();
		}
		
		
	}
	
	private void fillUpProfile() {

		int totalHashrate = 0;
		
		txtConfirmedReward.setText(profile.getConfirmed_reward() + " BTC");
		txtTotalHashrate.setText(App.formatHashRate(totalHashrate));
		txtAverageHashrate.setText(App.formatHashRate(profile.getHashrate()));

	}

	private void fillUpStats() {
		try {
			// TODO exeception handling
			Date started = App.dateStatsFormat.parse(stats.getRound_started());
			txtRoundStarted.setText(App.dateFormat.format(started));

			Date average = getAverageRoundTime(App.parseBlocks(stats.getBlocks()));
			txtAverageDuration.setText(App.dateDurationFormat.format(average));

			Date duration = App.dateDurationFormat.parse(stats.getRound_duration());
			txtRoundDuration.setText(App.dateDurationFormat.format(duration));

			double rating = calculateRoundRating(average, duration);

			setRatingBar(rating);

			float cdf = Float.valueOf(stats.getShares_cdf());
			System.out.println(cdf);
			float estimated = (duration.getTime() / (cdf / 100));

			txtEstimatedDuration.setText(App.dateDurationFormat.format(new Date((long) estimated)));

		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		float lastLuck24 = App.getInstance().getLuck24();
		float lastLuck7d = App.getInstance().getLuck7d();
		float lastLuck30d = App.getInstance().getLuck30d();

		float currentLuck24 = Float.parseFloat(stats.getLuck_1());
		float currentLuck7d = Float.parseFloat(stats.getLuck_7());
		float currentLuck30d = Float.parseFloat(stats.getLuck_30());

		setLuck(txtLuck24h, lastLuck24, currentLuck24);
		setLuck(txtLuck7d, lastLuck7d, currentLuck7d);
		setLuck(txtLuck30d, lastLuck30d, currentLuck30d);

		App.getInstance().setLuck24(currentLuck24);
		App.getInstance().setLuck7d(currentLuck7d);
		App.getInstance().setLuck30d(currentLuck30d);
	}
	
	private void fillUpPrices() {

		Price lastPrice = App.getInstance().getLastPrice();
		Price currentPrice = App.parsePrices(prices.getData()).getLastPrice();

		setPrice(txtCurrentValue, lastPrice, currentPrice);

		App.getInstance().setLastPrice(currentPrice);
	}

	public void updateCurrentTotalHashrate(int hashrate) {
		txtTotalHashrate.setText(App.formatHashRate(hashrate));
	}

}
