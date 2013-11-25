package com.eiabea.btcdroid.widget;

import java.util.Calendar;
import java.util.Random;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.util.App;

public class UpdateService extends Service implements Listener<Prices>{
	private static final String LOG = "de.vogella.android.widget.example";

	private Intent intent;
	
	private AppWidgetManager appWidgetManager;
	
	private int[] allWidgetIds;
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(LOG, "Called");
		// create some random data
		
		this.intent = intent;

		appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

		allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(), WidgetProvider.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
		Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));
		
		for (int widgetId : allWidgetIds) {
			// create some random data
			RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
			remoteViews.setTextViewText(R.id.txt_widget_price, "loading...");
			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), WidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.txt_widget_price, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
		App.getInstance().httpWorker.getPrices(this, null);
		
		stopSelf();

	}
	
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onResponse(Prices prices) {
		
		Price currentPrice = App.parsePrices(prices.getData()).getLastPrice();
		
		ComponentName thisWidget = new ComponentName(getApplicationContext(), WidgetProvider.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
		Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));
		
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
			
			// Set the text
			setPrice(remoteViews, currentPrice);

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), WidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.txt_widget_price, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
	}
	
	private void setPrice(RemoteViews views, Price current) {
		
		int txtId = R.id.txt_widget_price;
		
		if (current != null) {
			float lastPriceFloat = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getFloat("txt_" + txtId + "_value", 0f);
			float currentPriceFloat = Float.parseFloat(current.getValue());

			int minuteThreshold = App.getInstance().getPriceThreshold();
			long threshold = minuteThreshold * 60 * 1000;

			long lastUpdated = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext()).getLong("txt_" + txtId, 0);

			long now = Calendar.getInstance().getTimeInMillis();

			if ((lastUpdated + threshold) < now) {

				Log.d(getClass().getSimpleName(), "threshold expired --> set colors for " + "txt_" + txtId);
				if (lastPriceFloat > currentPriceFloat) {
					views.setTextColor(txtId, getApplicationContext().getResources().getColor(R.color.bd_red));
				} else if (lastPriceFloat < currentPriceFloat) {
					views.setTextColor(txtId, getApplicationContext().getResources().getColor(R.color.bd_green));
				} else {
					views.setTextColor(txtId, getApplicationContext().getResources().getColor(R.color.bd_black));
				}
			}

			views.setTextViewText(txtId, "" + lastPriceFloat);
		}
	}
}
