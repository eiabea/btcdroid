package com.eiabea.btcdroid.widget;

import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.service.ProfileUpdateService;
import com.eiabea.btcdroid.util.App;

public class RoundDurationWidgetProvider extends AppWidgetProvider {
	public static final String PARAM_STATS = "param_stats";

	private static final String ACTION_CLICK = "ACTION_CLICK";
	public static final String LOADING_FAILED = "ACTION_FAILED";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		try {
			ProfileUpdateService.getInstance().getStatsWidgets();
		} catch (Exception ignore) {
		}

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, RoundDurationWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			Intent intent = new Intent(context, RoundDurationWidgetProvider.class);

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
				ProfileUpdateService.getInstance().getStatsWidgets();
			} else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
				Stats stats = intent.getParcelableExtra(PARAM_STATS);

				Date duration = null;

				try {
					duration = App.dateDurationFormat.parse(stats.getRound_duration());
					remoteViews.setTextViewText(R.id.txt_widget_value, App.dateDurationFormat.format(duration));
					remoteViews.setTextColor(R.id.txt_widget_value, context.getResources().getColor(R.color.bd_green));
					remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_round_duration_widget));
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}

				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);

			} else if (intent.getAction().equals(LOADING_FAILED)) {
				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
			}

			ComponentName widget = new ComponentName(context, RoundDurationWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

		} catch (NullPointerException e) {
			remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
		}

	}

}