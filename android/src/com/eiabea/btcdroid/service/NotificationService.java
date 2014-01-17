package com.eiabea.btcdroid.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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

	public static final int INTERVAL_MIN = 30;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(getClass().getSimpleName(), "onCreate");

		ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

		// This schedule a runnable task every 2 minutes
		scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				getProfile();
			}
		}, 0, INTERVAL_MIN, TimeUnit.MINUTES);

		// getProfile();
	}

	private void getProfile() {
		Log.d(getClass().getSimpleName(), "get Profile");

		String url = HttpWorker.PROFILE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, this, this));

	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResponse(Profile response) {
		// TODO Auto-generated method stub
		List<Worker> workers = response.getWorkersList();

		int totalHashrate = 0;

		for (Worker tmp : workers) {
			totalHashrate += tmp.getHashrate();
		}
		
		if(totalHashrate < 27000){
			createNotification();
		}

		Log.d(getClass().getSimpleName(), "onResponse!: " + totalHashrate);
	}

	public void createNotification() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher) // notification
																														// icon
		.setContentTitle("Hashrate dropped!") // title for notification
		.setContentText("The Hashrate dropped") // message for notification
		.setAutoCancel(true); // clear notification after click
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());

	}

}
