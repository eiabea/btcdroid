package com.eiabea.btcdroid.util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

// TODO Errorhandling
// TODO run service only sometimes
// TODO on reboot --> register service

public class UpdateService extends Service {


	public static final String NOTIFICATION = "com.eiabea.btcdroid.util.UpdateService";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		
		Log.i(getClass().getSimpleName(), "onCreate");
		super.onCreate();
	}

//	public void resetInterval() {
//		int interval = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("update_interval_preference", "0"));
//		if (interval > 0) {
//			timer = new Timer();
//			Log.i(getClass().getSimpleName(), "Service started");
//			timer.scheduleAtFixedRate(new TimerTask() {
//				public void run() {
//					onTimerTick();
//				}
//			}, 0, interval * 1000L);
//			isRunning = true;
//
//		}
//
//	}
//
//	private void onTimerTick() {
//		try {
//			reloadData(false);
//
//		} catch (Throwable t) { // you should always ultimately catch all
//								// exceptions in timer tasks.
//			Log.e("TimerTick", "Timer Tick Failed.", t);
//		}
//	}

	@Override
	public void onDestroy() {
//		super.onDestroy();
//		if (timer != null) {
//			timer.cancel();
//			timer = null;
//			Log.i(getClass().getSimpleName(), "Service closed");
//		}
//		Log.i(getClass().getSimpleName(), "Service closed");
//		isRunning = false;
	}

//	public static boolean isRunning() {
//		return isRunning;
//	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("UpdateService", "Received start id " + startId + ": " + intent);
		reloadData(true);
		return START_NOT_STICKY; // run until explicitly stopped.
	}

	public void reloadData(boolean force) {
		if (App.getInstance().isTokenSet()) {

			getProfile();

			getStats();

			getPrices();
		}

	}

	private void getPrices() {

		Log.d(getClass().getSimpleName(), "Getting Prices");

		App.getInstance().httpWorker.getPricesMtGox(new Listener<PricesMtGox>() {

			@Override
			public void onResponse(PricesMtGox prices) {
//				sendPrices(prices);
			    Intent intent = new Intent(NOTIFICATION);
			    intent.putExtra(MainActivity.BROADCAST_TYPE, MainActivity.BROADCAST_PRICE);
			    intent.putExtra(MainActivity.BROADCAST_DATA, prices);
			    sendBroadcast(intent);
			    
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_prices", App.getInstance().gson.toJson(prices)).commit();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				Intent intent = new Intent(NOTIFICATION);
				intent.putExtra(MainActivity.BROADCAST_TYPE, MainActivity.BROADCAST_PRICE);
				sendBroadcast(intent);
//				sendPrices(null);
				// Toast.makeText(UpdateService.this,
				// App.getResString(R.string.txt_error_loading_price,
				// UpdateService.this), Toast.LENGTH_SHORT).show();
				Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_price, UpdateService.this));

			}
		});

	}

	private void getStats() {
		Log.d(getClass().getSimpleName(), "Getting Stats");

		App.getInstance().httpWorker.getStats(new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {

//				sendStats(stats);
			    Intent intent = new Intent(NOTIFICATION);
			    intent.putExtra(MainActivity.BROADCAST_TYPE, MainActivity.BROADCAST_STATS);
			    intent.putExtra(MainActivity.BROADCAST_DATA, stats);
			    sendBroadcast(intent);
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_stats", App.getInstance().gson.toJson(stats)).commit();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
//				sendStats(null);
				Intent intent = new Intent(NOTIFICATION);
				intent.putExtra(MainActivity.BROADCAST_TYPE, MainActivity.BROADCAST_STATS);
				sendBroadcast(intent);
				Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_stats, UpdateService.this));
				// Toast.makeText(UpdateService.this,
				// App.getResString(R.string.txt_error_loading_stats,
				// UpdateService.this), Toast.LENGTH_SHORT).show();

			}
		});
	}

	private void getProfile() {
		Log.d(getClass().getSimpleName(), "Getting Profile");

		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {

//				sendProfile(profile);
			    Intent intent = new Intent(NOTIFICATION);
			    intent.putExtra(MainActivity.BROADCAST_TYPE, MainActivity.BROADCAST_PROFILE);
			    intent.putExtra(MainActivity.BROADCAST_DATA, profile);
			    sendBroadcast(intent);
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_profile", App.getInstance().gson.toJson(profile)).commit();

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				Intent intent = new Intent(NOTIFICATION);
				intent.putExtra(MainActivity.BROADCAST_TYPE, MainActivity.BROADCAST_PROFILE);
				sendBroadcast(intent);
//				sendProfile(null);
				Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_profile, UpdateService.this));
				// Toast.makeText(UpdateService.this,
				// App.getResString(R.string.txt_error_loading_profile,
				// UpdateService.this), Toast.LENGTH_SHORT).show();

			}
		});
	}

}