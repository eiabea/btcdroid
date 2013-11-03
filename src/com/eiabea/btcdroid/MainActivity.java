package com.eiabea.btcdroid;

import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.views.WorkerView;

public class MainActivity extends ActionBarActivity {

	private static final int INTENT_ADD_POOL = 0;

	private MenuItem itemRefresh;

	private boolean statsLoaded = false;
	private boolean profileLoaded = false;
	private boolean pricesLoaded = true;

	private TextView txtNoPools, txtCurrentValue, txtTotalHashrate, txtRoundStarted,
			txtRoundDuration, txtLuck24h, txtLuck7d, txtLuck30d;
	private LinearLayout llInfoHolder, llWorkerHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUi();

		setListeners();

		reloadData();
//
//		App.getInstance().httpWorker.getPrices(new Listener<JsonObject>() {
//
//			@Override
//			public void onResponse(JsonObject json) {
//				Prices prices = App.parsePrices(json);
//
//					System.out.println(prices.getLastPrice().getValue());
////				for (Price price : prices.getPrices()) {
////					System.out.println(price.getCurrency() + price.getT7d());
////				}
//
//			}
//
//		});

	}

	private void getProfile() {
		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {

				clearWorkerViews();

				ArrayList<Worker> list = profile.getWorkersList();

				int totalHashrate = 0;

				for (Worker tmp : list) {
					WorkerView workerView = new WorkerView(MainActivity.this);
					workerView.setData(tmp);
					llWorkerHolder.addView(workerView);

					totalHashrate += tmp.getHashrate();

				}

				txtTotalHashrate.setText(String.valueOf(totalHashrate) + " MH/s");

				profileLoaded = true;

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
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}

				txtRoundDuration.setText(stats.getRound_duration());
				txtLuck24h.setText(formatProcent(stats.getLuck_1()));
				txtLuck7d.setText(formatProcent(stats.getLuck_7()));
				txtLuck30d.setText(formatProcent(stats.getLuck_30()));

				statsLoaded = true;

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
				
				if(lastPrice != null && currentPrice != null){
					float lastPriceFloat = Float.parseFloat(lastPrice.getValue());
					float currentPriceFloat = Float.parseFloat(currentPrice.getValue());
					
					if(lastPriceFloat > currentPriceFloat){
						txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_red));
					}else if (lastPriceFloat < currentPriceFloat){
						txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_green));
					}else{
						txtCurrentValue.setTextColor(getResources().getColor(R.color.bd_black));
						
					}
					
					txtCurrentValue.setText(currentPrice.getDisplay_short());
				}else if(currentPrice != null){
					txtCurrentValue.setText(currentPrice.getDisplay_short());
					
				}
				App.getInstance().setLastPrice(currentPrice);
				
				
				pricesLoaded = true;
				
				readyLoading();
				
			}
			
		});
	}

	private String formatProcent(String raw) {
		float fl = Float.parseFloat(raw);
		return String.valueOf(fl * 100) + " %";
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
		txtNoPools = (TextView) findViewById(R.id.txt_main_no_pools);

		txtCurrentValue = (TextView) findViewById(R.id.txt_main_info_current_value);
		txtTotalHashrate = (TextView) findViewById(R.id.txt_main_info_total_hashrate);
		txtRoundStarted = (TextView) findViewById(R.id.txt_main_info_round_started);
		txtRoundDuration = (TextView) findViewById(R.id.txt_main_info_round_duration);
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
			//Toast.makeText(this, "No Token set", Toast.LENGTH_SHORT).show();
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
