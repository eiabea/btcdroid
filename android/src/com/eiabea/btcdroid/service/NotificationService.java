package com.eiabea.btcdroid.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.GsonRequest;
import com.eiabea.btcdroid.util.HttpWorker;

public class NotificationService extends Service implements Listener<Profile>,
		ErrorListener {

	private SharedPreferences pref;

	private static NotificationService me;

	private ScheduledExecutorService scheduleTaskExecutor;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		me = this;

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		pref.edit().putInt("notification_last_hashrate", 0).commit();

		Log.d(getClass().getSimpleName(), "onCreate");

		startInterval();
	}

	public static NotificationService getInstance() {
		return me;
	}

	public void startInterval() {

		if (scheduleTaskExecutor != null) {
			scheduleTaskExecutor.shutdownNow();
		}

		int interval = Integer.valueOf(pref.getString("notification_interval", "60"));

		scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (App.getInstance().isTokenSet()) {
					getProfile();
				} else {
					Log.d(getClass().getSimpleName(), "No Token set");
				}
			}
		}, interval, interval, TimeUnit.MINUTES);
	}
	
	public void stopInterval(){
		if (scheduleTaskExecutor != null) {
			scheduleTaskExecutor.shutdownNow();
		}
	}

	private void getProfile() {
		Log.d(getClass().getSimpleName(), "get Profile");

		String url = HttpWorker.PROFILE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, this, this));

	}

	@Override
	public void onErrorResponse(VolleyError error) {

	}

	@Override
	public void onResponse(Profile response) {
		List<Worker> workers = response.getWorkersList();

		int totalHashrate = 0;
		int limit = Integer.valueOf(pref.getString("notification_hashrate", "0"));

		for (Worker tmp : workers) {
			totalHashrate += tmp.getHashrate();
		}

		if (limit > 0 && totalHashrate < limit) {
			createFirstDropNotification(totalHashrate);
		}

		Log.d(getClass().getSimpleName(), "onResponse!: " + totalHashrate);
	}

	public void createFirstDropNotification(int hashrate) {
		// if (!alreadyShown) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(getString(R.string.txt_hashrate_dropped_title))
			.setContentText(String.format(getString(R.string.txt_hashrate_dropped_message), App.formatHashRate(hashrate)))
			.setAutoCancel(true);

		Intent deleteIntent = new Intent(this, OnDeleteReceiver.class);
		deleteIntent.setAction("delete");
		PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		mBuilder.setContentIntent(pi);
		mBuilder.setDeleteIntent(pendingDeleteIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = setDefaults(mBuilder.build());

		mNotificationManager.notify(0, notif);

	}

	private Notification setDefaults(Notification notification) {
		boolean sound = pref.getBoolean("notification_sound", true);
		boolean vibrate = pref.getBoolean("notification_vibrate", false);
		boolean led = pref.getBoolean("notification_led", false);

		if (sound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}

		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		if (led) {
			notification.ledARGB = 0xffff8b00;
			notification.ledOnMS = 300;
			notification.ledOffMS = 1000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}

		return notification;
	}

}
