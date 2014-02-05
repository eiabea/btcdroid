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
import com.eiabea.btcdroid.widget.WidgetProvider;

public class ProfileUpdateService extends Service implements ErrorListener {

	private SharedPreferences pref;

	private static ProfileUpdateService me;

	private ScheduledExecutorService scheduleNotification, scheduleWidgets;
	
//	private LoadingInterface loadingInterface;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		me = this;

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		Log.d(getClass().getSimpleName(), "onCreate");

		start();
	}

	public static ProfileUpdateService getInstance() {
		return me;
	}

	public void start() {
		startNotification();
		startWidgets();
	}
	
	public void startNotification() {
		if (scheduleNotification != null) {
			scheduleNotification.shutdownNow();
		}
		
		int intervalNotificaion = Integer.valueOf(pref.getString("notification_interval", "60"));

		scheduleNotification = Executors.newScheduledThreadPool(5);

		scheduleNotification.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (App.getInstance().isTokenSet()) {
					getProfileNotification();
				} else {
					Log.d(getClass().getSimpleName(), "No Token set");
				}
			}
		}, intervalNotificaion, intervalNotificaion, TimeUnit.MINUTES);
	}
	
	public void startWidgets() {
		if (scheduleWidgets != null) {
			scheduleWidgets.shutdownNow();
		}
		
		int intervalWidget = Integer.valueOf(pref.getString("widget_interval", "30"));
		scheduleWidgets = Executors.newScheduledThreadPool(4);
		
		scheduleWidgets.scheduleAtFixedRate(new Runnable() {
			public void run() {
				if (App.getInstance().isTokenSet()) {
					getProfileWidgets();
				} else {
					Log.d(getClass().getSimpleName(), "No Token set");
				}
			}
		}, 0, intervalWidget, TimeUnit.MINUTES);
	}
	
	public void stop(){
		stopNotification();
		stopWidgets();
	}
	
	public void stopNotification(){
		if (scheduleNotification != null) {
			scheduleNotification.shutdownNow();
		}
	}
	
	public void stopWidgets(){
		if (scheduleWidgets != null) {
			scheduleWidgets.shutdownNow();
		}
	}

	public void getProfileWidgets() {
		Log.d(getClass().getSimpleName(), "get Profile Widgets");

		String url = HttpWorker.PROFILE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {
//			    Intent i = new Intent(getApplicationContext(), WidgetProvider.class);
//			    i.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//			    i.putExtra(WidgetProvider.PARAM_PROFILE, response);
//			    getApplicationContext().sendBroadcast(i);	
//			    
//			    Intent dashclockIntent = new Intent(DashClockWidget.UPDATE_DASHCLOCK);
//			    dashclockIntent.putExtra(WidgetProvider.PARAM_PROFILE, response);
//			    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(dashclockIntent);
			    Log.d(getClass().getSimpleName(), "onResponse Widgets");
			    App.updateWidgets(getApplicationContext(), profile);
			}
		}, this));

	}
	
	public void getProfileNotification() {
		Log.d(getClass().getSimpleName(), "get Profile Notification");
		
		String url = HttpWorker.PROFILE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");
		
		System.out.println(HttpWorker.mQueue.toString());
		
		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, new Listener<Profile>() {

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
				Log.d(getClass().getSimpleName(), "onResponse Notification");
			}
		}, this));
		
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		// Update Widget
	    Intent i = new Intent(getApplicationContext(), WidgetProvider.class);
	    i.setAction(WidgetProvider.LOADING_FAILED);
	    getApplicationContext().sendBroadcast(i);	

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
	
	public interface LoadingInterface{
		public void onResponse(Profile profile);
	}

}
