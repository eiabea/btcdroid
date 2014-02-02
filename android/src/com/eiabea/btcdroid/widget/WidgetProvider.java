package com.eiabea.btcdroid.widget;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.service.NotificationService;
import com.eiabea.btcdroid.util.App;

public class WidgetProvider extends AppWidgetProvider {
	public static final String PARAM_PROFILE = "param_profile";

	private static final String ACTION_CLICK = "ACTION_CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			// create some random data

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			Intent intent = new Intent(context, WidgetProvider.class);

			intent.setAction(ACTION_CLICK);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.rl_widget_holder, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		try {

			if (intent.getAction().equals(ACTION_CLICK) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
				NotificationService.getInstance().getProfile();
			} else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
				Profile profile = intent.getParcelableExtra(PARAM_PROFILE);
				
				ArrayList<Worker> list = profile.getWorkersList();

				int totalHashrate = 0;

				for (Worker tmp : list) {
					totalHashrate += tmp.getHashrate();
				}

				remoteViews.setTextViewText(R.id.txt_main_info_total_hashrate, App.formatHashRate(totalHashrate));
				remoteViews.setTextViewText(R.id.txt_main_info_average_hashrate, App.formatHashRate(profile.getHashrate()));

				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);

			} 

			ComponentName widget = new ComponentName(context, WidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

		} catch (NullPointerException e) {
			remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
		}

	}

}
