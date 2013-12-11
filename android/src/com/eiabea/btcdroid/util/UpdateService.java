package com.eiabea.btcdroid.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.PricesBitstamp;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

// TODO Errorhandling
// TODO run service only sometimes
// TODO on reboot --> register service

public class UpdateService extends Service {

	private static boolean isRunning = false;
	private Timer timer;

	private ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_PRICES = 3;
	public static final int MSG_STATS = 4;
	public static final int MSG_PROFILE = 5;
	public static final int MSG_PRICES_BITSTAMP = 6;
	public static final String MSG_PRICES_PARAM = "param_prices";
	public static final String MSG_STATS_PARAM = "param_stats";
	public static final String MSG_PROFILE_PARAM = "param_profile";
	final Messenger mMessenger = new Messenger(new IncomingHandler(this));

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}
	
	static class IncomingHandler extends Handler {
		private final WeakReference<UpdateService> mLink;

		IncomingHandler(UpdateService service) {
			mLink = new WeakReference<UpdateService>(service);
		}

		@Override
		public void handleMessage(Message msg) {
			UpdateService activity = mLink.get();
			if (activity != null) {
				switch (msg.what) {
				case 1337:
					activity.reloadData(false);
					break;
				case MSG_REGISTER_CLIENT:
					activity.mClients.add(msg.replyTo);
					break;
				case MSG_UNREGISTER_CLIENT:
					activity.mClients.remove(msg.replyTo);
					break;
				default:
					super.handleMessage(msg);
				}
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		resetInterval();
		
		reloadData(true);
	}

	public void resetInterval() {
		int interval = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("update_interval_preference", "0"));
		if (interval > 0) {
			timer = new Timer();
			Log.i(getClass().getSimpleName(), "Service started");
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					onTimerTick();
				}
			}, 0, interval * 1000L);
			isRunning = true;

		}

	}

	private void onTimerTick() {
		try {
			reloadData(false);

		} catch (Throwable t) { // you should always ultimately catch all
								// exceptions in timer tasks.
			Log.e("TimerTick", "Timer Tick Failed.", t);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
			timer = null;
			Log.i(getClass().getSimpleName(), "Service closed");
		}
		Log.i(getClass().getSimpleName(), "Service closed");
		isRunning = false;
	}

	public static boolean isRunning() {
		return isRunning;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("UpdateService", "Received start id " + startId + ": " + intent);
		return START_STICKY; // run until explicitly stopped.
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

		if(false){
			App.getInstance().httpWorker.getPricesMtGox(new Listener<PricesMtGox>() {
				
				@Override
				public void onResponse(PricesMtGox prices) {
					sendPrices(prices);
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_prices", App.getInstance().gson.toJson(prices)).commit();
				}
				
			}, new ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					
					sendPrices(null);
//				Toast.makeText(UpdateService.this, App.getResString(R.string.txt_error_loading_price, UpdateService.this), Toast.LENGTH_SHORT).show();
					Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_price, UpdateService.this));
					
				}
			});
			
		}else{
			App.getInstance().httpWorker.getPricesBitstamp(new Listener<PricesBitstamp>() {
				
				@Override
				public void onResponse(PricesBitstamp prices) {
					sendPricesBitstamp(prices);
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_prices_bitstamp", App.getInstance().gson.toJson(prices)).commit();
				}
				
			}, new ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					
					sendPricesBitstamp(null);
//				Toast.makeText(UpdateService.this, App.getResString(R.string.txt_error_loading_price, UpdateService.this), Toast.LENGTH_SHORT).show();
					Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_price, UpdateService.this));
					
				}
			});
		}
	}

	private void getStats() {
		Log.d(getClass().getSimpleName(), "Getting Stats");

		App.getInstance().httpWorker.getStats(new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {

				sendStats(stats);
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_stats", App.getInstance().gson.toJson(stats)).commit();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				sendStats(null);
				Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_stats, UpdateService.this));
//				Toast.makeText(UpdateService.this, App.getResString(R.string.txt_error_loading_stats, UpdateService.this), Toast.LENGTH_SHORT).show();

			}
		});
	}
	
	private void getProfile() {
		Log.d(getClass().getSimpleName(), "Getting Profile");

		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {
				
				sendProfile(profile);
				PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("pref_profile", App.getInstance().gson.toJson(profile)).commit();
			
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				sendProfile(null);
				Log.i(getClass().getSimpleName(), "" + App.getResString(R.string.txt_error_loading_profile, UpdateService.this));
//				Toast.makeText(UpdateService.this, App.getResString(R.string.txt_error_loading_profile, UpdateService.this), Toast.LENGTH_SHORT).show();

			}
		});
	}

	private void sendPrices(PricesMtGox prices) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as a String
				Bundle b = new Bundle();
				b.putParcelable(MSG_PRICES_PARAM, prices);
				Message msg = Message.obtain(null, MSG_PRICES);
				msg.setData(b);
				mClients.get(i).send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}

	}
	
	private void sendPricesBitstamp(PricesBitstamp prices) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as a String
				Bundle b = new Bundle();
				b.putParcelable(MSG_PRICES_PARAM, prices);
				Message msg = Message.obtain(null, MSG_PRICES_BITSTAMP);
				msg.setData(b);
				mClients.get(i).send(msg);
				
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
		
	}

	private void sendStats(Stats stats) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as a String
				Bundle b = new Bundle();
				b.putParcelable(MSG_STATS_PARAM, stats);
				Message msg = Message.obtain(null, MSG_STATS);
				msg.setData(b);
				mClients.get(i).send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}

	}
	
	private void sendProfile(Profile profile) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				// Send data as a String
				Bundle b = new Bundle();
				b.putParcelable(MSG_PROFILE_PARAM, profile);
				Message msg = Message.obtain(null, MSG_PROFILE);
				msg.setData(b);
				mClients.get(i).send(msg);
				
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				mClients.remove(i);
			}
		}
		
	}

//	private void sendMessageToUI(int intvaluetosend) {
//		for (int i = mClients.size() - 1; i >= 0; i--) {
//			try {
//				// Send data as an Integer
//				mClients.get(i).send(Message.obtain(null, MSG_SET_INT_VALUE, intvaluetosend, 0));
//
//				// Send data as a String
//				Bundle b = new Bundle();
//				b.putString("str1", "ab" + intvaluetosend + "cd");
//				Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
//				msg.setData(b);
//				mClients.get(i).send(msg);
//
//			} catch (RemoteException e) {
//				// The client is dead. Remove it from the list; we are going
//				// through the list from back to front so this is safe to do
//				// inside the loop.
//				mClients.remove(i);
//			}
//		}
//	}

}