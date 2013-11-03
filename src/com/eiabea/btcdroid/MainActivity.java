package com.eiabea.btcdroid;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.Listener;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.views.WorkerView;
import com.google.gson.JsonObject;

public class MainActivity extends ActionBarActivity {

	private static final int INTENT_ADD_POOL = 0;

	private TextView txtNoPools;
	private LinearLayout llInfoHolder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUi();

		setListeners();

		reloadData();
		
		App.getInstance().httpWorker.getPrices(new Listener<JsonObject>() {

			@Override
			public void onResponse(JsonObject json) {
				Prices prices = App.parsePrices(json);
				
				for(Price price : prices.getPrices()){
					System.out.println(price.getCurrency() + price.getT7d());
				}
				
//				ArrayList<Worker> list = profile.getWorkersList();
//
//				for (Worker tmp : list) {
//					WorkerView workerView = new WorkerView(MainActivity.this);
//					workerView.setData(tmp);
//					llInfoHolder.addView(workerView);
//					
//					Log.d(getClass().getSimpleName(),
//							"Worker: " + tmp.getName() + "; " + tmp.getScore());
//
//				}
//				showProgress(false);
//				showInfos();

			}

		});

	}

	private void getProfile() {
		showProgress(true);
		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {
				ArrayList<Worker> list = profile.getWorkersList();

				for (Worker tmp : list) {
					WorkerView workerView = new WorkerView(MainActivity.this);
					workerView.setData(tmp);
					llInfoHolder.addView(workerView);
					
					Log.d(getClass().getSimpleName(),
							"Worker: " + tmp.getName() + "; " + tmp.getScore());

				}
				showProgress(false);
				showInfos();

			}

		});
	}

	private void initUi() {
		txtNoPools = (TextView) findViewById(R.id.txt_main_no_pools);

		llInfoHolder = (LinearLayout) findViewById(R.id.ll_main_info_holder);
	}

	private void setListeners() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_pool:
			startActivityForResult(new Intent(this, AddPoolActivity.class), INTENT_ADD_POOL);
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
			getProfile();
		} else {
			hideInfos();
			Toast.makeText(this, "No Token set", Toast.LENGTH_SHORT).show();
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
		setSupportProgressBarIndeterminateVisibility(show);
	}

}
