package com.eiabea.btcdroid.widget;

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
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.util.App;

public class AverageHashrateWidgetProvider extends AppWidgetProvider {
	public static final String PARAM_PROFILE = "param_profile";

	private static final String ACTION_CLICK = "ACTION_CLICK";
	public static final String LOADING_FAILED = "ACTION_FAILED";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		try{
			UpdateService.getInstance().getProfileWidgets();
		}catch(Exception ignore){
		}

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, AverageHashrateWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

			Intent intent = new Intent(context, AverageHashrateWidgetProvider.class);

			intent.setAction(ACTION_CLICK);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.rl_widget_holder, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		try {

			if (intent.getAction().equals(ACTION_CLICK) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
				UpdateService.getInstance().getProfileWidgets();
			} else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
				Profile profile = intent.getParcelableExtra(PARAM_PROFILE);

				remoteViews.setTextViewText(R.id.txt_widget_value, App.formatHashRate(profile.getHashrate()));
				remoteViews.setTextColor(R.id.txt_widget_value, context.getResources().getColor(R.color.bd_dark_grey_text));
				remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_average_total_hashrate));

				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);

			} else if (intent.getAction().equals(LOADING_FAILED)) {
				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
			} else{
				super.onReceive(context, intent);
			}
			
			ComponentName widget = new ComponentName(context, AverageHashrateWidgetProvider.class);
			AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

		} catch (NullPointerException e) {
			remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
			super.onReceive(context, intent);
		} 

	}

}
