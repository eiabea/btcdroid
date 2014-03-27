package com.eiabea.btcdroid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.util.App;

public class MultiWidgetProvider extends AppWidgetProvider {
	public static final String PARAM_PROFILE = "param_profile";
	public static final String PARAM_STATS = "param_stats";

	private static final String ACTION_CLICK = "ACTION_CLICK";
	public static final String LOADING_FAILED = "ACTION_FAILED";

	private Intent intent;
	
	private static boolean profileLoaded = false, statsLoaded = false;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, MultiWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_multi_layout);
			try {

				if (intent.getAction().equals(ACTION_CLICK) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
					remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
					Intent i = new Intent(context, UpdateService.class);
					context.startService(i);
				} else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
					Profile profile = intent.getParcelableExtra(PARAM_PROFILE);
					Stats stats = intent.getParcelableExtra(PARAM_STATS);
					
					if(profile != null){
						remoteViews.setTextViewText(R.id.txt_widget_current_hashrate, App.formatHashRate(App.getTotalHashrate(profile)));
						
						float estimated = Float.valueOf(profile.getEstimated_reward());
						remoteViews.setTextViewText(R.id.txt_widget_estimated_reward, App.formatReward(estimated));
						profileLoaded = true;
					}
					
					if(stats != null){
						remoteViews.setTextViewText(R.id.txt_widget_current_round_duration, stats.getRound_duration());
						statsLoaded = true;
					}

					if(profileLoaded && statsLoaded){
						remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
						profileLoaded = statsLoaded = false;
					}

				} else if (intent.getAction().equals(LOADING_FAILED)) {
					remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
					profileLoaded = statsLoaded = false;
				} 

				ComponentName widget = new ComponentName(context, MultiWidgetProvider.class);
				AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

			} catch (NullPointerException e) {
				remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
				Log.e(getClass().getSimpleName(), "Something was null, damn! (NullPointer)");
			}
			
			Intent intent = new Intent(context, MultiWidgetProvider.class);
			intent.setAction(ACTION_CLICK);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.rl_widget_holder, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		this.intent = intent;
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, MultiWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		onUpdate(context, appWidgetManager, allWidgetIds);

	}

}
