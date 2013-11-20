package com.eiabea.btcdroid;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.views.WorkerView;

public class MainActivity extends ActionBarActivity {

	private static final int INTENT_ADD_POOL = 0;

	private MenuItem itemRefresh, itemAdd;

	private boolean statsLoaded = false;
	private boolean profileLoaded = false;
	private boolean pricesLoaded = true;

	private TextView txtNoPools, txtConfirmedReward, txtCurrentValue,
			txtTotalHashrate, txtAverageHashrate, txtRoundStarted,
			txtRoundDuration, txtAverageDuration, txtLuck24h, txtLuck7d,
			txtLuck30d;
	private LinearLayout llInfoHolder, llWorkerHolder;
	private RatingBar ratRating;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUi();

		setListeners();

		reloadData();

	}

	
	
	@Override
	protected void onResume() {
		supportInvalidateOptionsMenu();
		super.onResume();
	}



	private void getProfile() {
		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {

				clearWorkerViews();

				ArrayList<Worker> list = profile.getWorkersList();

				Collections.sort(list, new App.sortWorkers());

				int totalHashrate = 0;

				for (Worker tmp : list) {
					WorkerView workerView = new WorkerView(MainActivity.this);
					workerView.setData(tmp);
					llWorkerHolder.addView(workerView);

					totalHashrate += tmp.getHashrate();

				}

				txtConfirmedReward.setText(profile.getConfirmed_reward() + " BTC");
				txtTotalHashrate.setText(App.formatHashRate(totalHashrate));
				txtAverageHashrate.setText(App.formatHashRate(profile.getHashrate()));

				profileLoaded = true;

				readyLoading();

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
		App.getInstance().httpWorker.getStats(new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {
				try {
					Date date = App.dateStatsFormat.parse(stats.getRound_started());
					txtRoundStarted.setText(App.dateFormat.format(date));

					Date average = getAverageRoundTime(App.parseBlocks(stats.getBlocks()));
					txtAverageDuration.setText(App.dateDurationFormat.format(average));

					Date duration = App.dateDurationFormat.parse(stats.getRound_duration());
					txtRoundDuration.setText(App.dateDurationFormat.format(duration));

					double rating = calculateRoundRating(average, duration);

					setRatingBar(rating);

				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}

				txtLuck24h.setText(formatProcent(stats.getLuck_1()));
				txtLuck7d.setText(formatProcent(stats.getLuck_7()));
				txtLuck30d.setText(formatProcent(stats.getLuck_30()));

				statsLoaded = true;

				readyLoading();

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
		App.getInstance().httpWorker.getPrices(new Listener<Prices>() {

			@Override
			public void onResponse(Prices prices) {

				Price lastPrice = App.getInstance().getLastPrice();
				Price currentPrice = App.parsePrices(prices.getData()).getLastPrice();

				if (lastPrice != null && currentPrice != null) {
					float lastPriceFloat = Float.parseFloat(lastPrice.getValue());
					float currentPriceFloat = Float.parseFloat(currentPrice.getValue());

					if (lastPriceFloat > currentPriceFloat) {
						txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_red));
					} else if (lastPriceFloat < currentPriceFloat) {
						txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_green));
					}
					// else{
					// txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_black));
					//
					// }

					txtCurrentValue.setText(currentPrice.getDisplay_short());
				} else if (currentPrice != null) {
					txtCurrentValue.setText(currentPrice.getDisplay_short());

				}
				App.getInstance().setLastPrice(currentPrice);

				pricesLoaded = true;

				readyLoading();

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

	private String formatProcent(String raw) {
		float fl = Float.parseFloat(raw);
		return String.format("%.1f", fl * 100) + " %";
	}

	private void clearWorkerViews() {
		if (llWorkerHolder.getChildCount() > 0) {
			llWorkerHolder.removeAllViews();
		}
	}

	private void readyLoading() {
		if (profileLoaded == true && statsLoaded == true && pricesLoaded == true) {
			showInfos();
			showProgress(false);
		}
	}

	private void initUi() {
		
		getSupportActionBar().setSubtitle("for Slush's Pool");
		
		txtNoPools = (TextView) findViewById(R.id.txt_main_no_pools);

		txtConfirmedReward = (TextView) findViewById(R.id.txt_main_info_confirmed_reward);
		txtCurrentValue = (TextView) findViewById(R.id.txt_main_info_current_value);
		txtTotalHashrate = (TextView) findViewById(R.id.txt_main_info_total_hashrate);
		txtAverageHashrate = (TextView) findViewById(R.id.txt_main_info_average_hashrate);
		txtRoundStarted = (TextView) findViewById(R.id.txt_main_info_round_started);
		txtRoundDuration = (TextView) findViewById(R.id.txt_main_info_round_duration);
		txtAverageDuration = (TextView) findViewById(R.id.txt_main_info_average_duration);
		ratRating = (RatingBar) findViewById(R.id.rat_main_info_rating);
		txtLuck24h = (TextView) findViewById(R.id.txt_main_info_luck_24h);
		txtLuck7d = (TextView) findViewById(R.id.txt_main_info_luck_7d);
		txtLuck30d = (TextView) findViewById(R.id.txt_main_info_luck_30d);

		llInfoHolder = (LinearLayout) findViewById(R.id.ll_main_info_holder);
		llWorkerHolder = (LinearLayout) findViewById(R.id.ll_main_worker_holder);
	}

	private void setListeners() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		itemRefresh = menu.findItem(R.id.action_refresh);
		itemAdd = menu.findItem(R.id.action_add_pool);
		
		if(App.getInstance().httpWorker.isTokenSet()){
			itemAdd.setVisible(false);
		}else{
			itemAdd.setVisible(true);
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
			reloadData();
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

				reloadData();
			}
			break;

		default:
			break;
		}

		super.onActivityResult(reqCode, resCode, intent);
	}

	private void reloadData() {
		if (App.getInstance().httpWorker.isTokenSet()) {
			showProgress(true);

			profileLoaded = pricesLoaded = statsLoaded = false;

			getProfile();
			getStats();
			getPrices();
		} else {
			hideInfos();
		}

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

		if (itemRefresh != null) {
			itemRefresh.setVisible(!show);
		}

		setSupportProgressBarIndeterminateVisibility(show);
	}

}
