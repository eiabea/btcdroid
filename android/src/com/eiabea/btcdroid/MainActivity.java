package com.eiabea.btcdroid;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.adapter.MainViewAdapter;
import com.eiabea.adapter.WorkerListAdapter;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class MainActivity extends ActionBarActivity {

	private static final int INTENT_ADD_POOL = 0;
	
	public static final int FRAGMENT_POOL = 0;
	public static final int FRAGMENT_WORKER = 1;

	private static final String STATE_PROFILE = "state_profile";
	private static final String STATE_STATS = "state_stats";
	private static final String STATE_PRICES = "state_prices";

	private MenuItem itemRefresh, itemAdd;
	
	private ViewPager viewPager;
	private MainViewAdapter adapter;

	private boolean statsLoaded = false;
	private boolean profileLoaded = false;
	private boolean pricesLoaded = false;

	private TextView txtNoPools, txtConfirmedReward, txtCurrentValue,
			txtTotalHashrate, txtAverageHashrate, txtRoundStarted,
			txtRoundDuration, txtEstimatedDuration, txtAverageDuration, txtLuck24h, txtLuck7d,
			txtLuck30d;
	private LinearLayout llInfoHolder; //, llWorkerHolder;
	private ExpandableListView exlvWOrkerHolder;
	private RatingBar ratRating;
	private boolean isProgessShowing = false;

	private Profile profile = null;
	private Stats stats = null;
	private Prices prices = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUi();

		setListeners();

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			this.profile = savedInstanceState.getParcelable(STATE_PROFILE);
			this.stats = savedInstanceState.getParcelable(STATE_STATS);
			this.prices = savedInstanceState.getParcelable(STATE_PRICES);
		}

		// TODO reenable
//		reloadData(false);

	}

	@Override
	protected void onResume() {
		supportInvalidateOptionsMenu();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putParcelable(STATE_PROFILE, this.profile);
		savedInstanceState.putParcelable(STATE_STATS, this.stats);
		savedInstanceState.putParcelable(STATE_PRICES, this.prices);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	private void getProfile() {

		profileLoaded = false;

		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {

				fillUpProfile(profile);

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				txtConfirmedReward.setText("- BTC");
				txtTotalHashrate.setText("- MH/s");
				txtAverageHashrate.setText("- MH/s");

				profileLoaded = true;

				Toast.makeText(MainActivity.this, "Error loading Profile", Toast.LENGTH_SHORT).show();

				readyLoading();
			}
		});
	}

	private void getStats() {

		statsLoaded = false;

		App.getInstance().httpWorker.getStats(new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {

				fillUpStats(stats);

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				txtRoundStarted.setText("-");
				txtAverageDuration.setText("-");
				txtRoundDuration.setText("-");
				txtLuck24h.setText("-");
				txtLuck7d.setText("-");
				txtLuck30d.setText("-");

				statsLoaded = true;

				Toast.makeText(MainActivity.this, "Error loading Stats", Toast.LENGTH_SHORT).show();

				readyLoading();
			}
		});
	}

	private void getPrices() {

		pricesLoaded = false;

		App.getInstance().httpWorker.getPrices(new Listener<Prices>() {

			@Override
			public void onResponse(Prices prices) {

				fillUpPrices(prices);

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				txtCurrentValue.setText("-");
				txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_black));

				pricesLoaded = true;

				Toast.makeText(MainActivity.this, "Error loading Price", Toast.LENGTH_SHORT).show();

				readyLoading();
			}
		});
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

//	private void clearWorkerViews() {
//		if (llWorkerHolder.getChildCount() > 0) {
//			llWorkerHolder.removeAllViews();
//		}
//	}

	private void readyLoading() {
		if (profileLoaded == true && statsLoaded == true && pricesLoaded == true) {
			showInfos();
			showProgress(false);
		}
	}

	private void initUi() {

		getSupportActionBar().setSubtitle("for Slush's Pool");
		
		viewPager = (ViewPager) findViewById(R.id.vp_main);
		adapter = new MainViewAdapter(this, getSupportFragmentManager());
		viewPager.setAdapter(adapter);

		txtNoPools = (TextView) findViewById(R.id.txt_main_no_pools);

		txtConfirmedReward = (TextView) findViewById(R.id.txt_main_info_confirmed_reward);
		txtCurrentValue = (TextView) findViewById(R.id.txt_main_info_current_value);
		txtTotalHashrate = (TextView) findViewById(R.id.txt_main_info_total_hashrate);
		txtAverageHashrate = (TextView) findViewById(R.id.txt_main_info_average_hashrate);
		txtRoundStarted = (TextView) findViewById(R.id.txt_main_info_round_started);
		txtRoundDuration = (TextView) findViewById(R.id.txt_main_info_round_duration);
		txtEstimatedDuration = (TextView) findViewById(R.id.txt_main_info_estimated_duration);
		txtAverageDuration = (TextView) findViewById(R.id.txt_main_info_average_duration);
		ratRating = (RatingBar) findViewById(R.id.rat_main_info_rating);
		txtLuck24h = (TextView) findViewById(R.id.txt_main_info_luck_24h);
		txtLuck7d = (TextView) findViewById(R.id.txt_main_info_luck_7d);
		txtLuck30d = (TextView) findViewById(R.id.txt_main_info_luck_30d);

		llInfoHolder = (LinearLayout) findViewById(R.id.ll_main_info_holder);
//		llWorkerHolder = (LinearLayout) findViewById(R.id.ll_main_worker_holder);
		exlvWOrkerHolder = (ExpandableListView) findViewById(R.id.exlv_main_worker_holder);
	}

	private void setListeners() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		itemRefresh = menu.findItem(R.id.action_refresh);
		itemAdd = menu.findItem(R.id.action_add_pool);

		if (App.getInstance().httpWorker.isTokenSet()) {
			itemAdd.setVisible(false);
		} else {
			itemAdd.setVisible(true);
		}

		if (!isProgessShowing) {
			itemRefresh.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_pool:
			startActivityForResult(new Intent(this, AddPoolActivity.class), INTENT_ADD_POOL);
			break;

		case R.id.action_refresh:
			reloadData(true);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent intent) {
		switch (reqCode) {
		case INTENT_ADD_POOL:
			if (resCode == RESULT_OK) {
				App.getInstance().httpWorker.setToken(intent.getExtras().getString("token"));

				reloadData(true);
			}
			break;

		default:
			break;
		}

		super.onActivityResult(reqCode, resCode, intent);
	}

	private void reloadData(boolean force) {
		if (App.getInstance().httpWorker.isTokenSet()) {

			if (this.profile == null || force) {
				showProgress(true);

				getProfile();
			} else {
				fillUpProfile(this.profile);
			}

			if (this.stats == null || force) {
				showProgress(true);

				getStats();
			} else {
				fillUpStats(this.stats);
			}

			if (this.prices == null || force) {
				showProgress(true);

				getPrices();
			} else {
				fillUpPrices(this.prices);
			}
		} else {
			hideInfos();
		}

	}

	private void fillUpPrices(Prices prices) {
		
		this.prices = prices;
		
		Price lastPrice = App.getInstance().getLastPrice();
		Price currentPrice = App.parsePrices(prices.getData()).getLastPrice();
		
		setPrice(txtCurrentValue, lastPrice, currentPrice);

		App.getInstance().setLastPrice(currentPrice);

		pricesLoaded = true;

		readyLoading();
	}

	private void fillUpStats(Stats stats) {
		
		this.stats = stats;
		
		try {
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

		statsLoaded = true;

		readyLoading();

	}

	private void setLuck(TextView txt, float last, float current) {
		if (last > 0 && current > 0) {

			if (last > current) {
				txt.setTextColor(getResources().getColor(R.color.bd_red));
			} else if (last < current) {
				txt.setTextColor(getResources().getColor(R.color.bd_green));
			}

			txt.setText(formatProcent(current));
		} else  {
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

	private void fillUpProfile(Profile profile) {

		this.profile = profile;

//		clearWorkerViews();

		ArrayList<Worker> list = profile.getWorkersList();

		Collections.sort(list, new App.sortWorkers());

		int totalHashrate = 0;
		
		WorkerListAdapter adapter = new WorkerListAdapter(this);
		
		adapter.setData(list);

		for (Worker tmp : list) {
//			WorkerView workerView = new WorkerView(MainActivity.this);
//			workerView.setData(tmp);
//			llWorkerHolder.addView(workerView);

			totalHashrate += tmp.getHashrate();

		}
		
		exlvWOrkerHolder.setAdapter(adapter);

		txtConfirmedReward.setText(profile.getConfirmed_reward() + " BTC");
		txtTotalHashrate.setText(App.formatHashRate(totalHashrate));
		txtAverageHashrate.setText(App.formatHashRate(profile.getHashrate()));

		profileLoaded = true;

		readyLoading();

	}

	private void showInfos() {
		if (txtNoPools.getVisibility() == View.VISIBLE) {
			txtNoPools.setVisibility(View.GONE);
		}
		if (llInfoHolder.getVisibility() == View.GONE) {
			llInfoHolder.setVisibility(View.VISIBLE);
		}
	}

	private void hideInfos() {
		if (txtNoPools.getVisibility() == View.GONE) {
			txtNoPools.setVisibility(View.VISIBLE);
		}
		if (llInfoHolder.getVisibility() == View.VISIBLE) {
			llInfoHolder.setVisibility(View.GONE);
		}
	}

	private void showProgress(boolean show) {

		this.isProgessShowing = show;

		if (itemRefresh != null) {
			itemRefresh.setVisible(!show);
		}

		setSupportProgressBarIndeterminateVisibility(show);
	}

}
