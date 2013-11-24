package com.eiabea.btcdroid.widget;

import java.util.Random;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Response.Listener;
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
			remoteViews.setTextViewText(R.id.update, "Reloading price...");
			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), WidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
		App.getInstance().httpWorker.getPrices(this, null);
		
//		stopSelf();

//		super.onStart(intent, startId);
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
			remoteViews.setTextViewText(R.id.update, "Price: " + currentPrice.getDisplay_short());

			// Register an onClickListener
			Intent clickIntent = new Intent(this.getApplicationContext(), WidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
		
	}
}
