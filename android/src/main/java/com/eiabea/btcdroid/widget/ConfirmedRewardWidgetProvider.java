package com.eiabea.btcdroid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.util.App;

public class ConfirmedRewardWidgetProvider extends AppWidgetProvider {
    public static final String PARAM_PROFILE = "param_profile";

    private static final String ACTION_CLICK = "ACTION_CLICK";
    public static final String LOADING_FAILED = "ACTION_FAILED";

    private Intent intent;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean trans = pref.getBoolean("transparent_widgets", false);

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, ConfirmedRewardWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            if(trans){
                remoteViews.setInt(R.id.ll_pool_hash_holder_left, "setBackgroundResource", R.color.bd_black_transparent);
            }else{
                remoteViews.setInt(R.id.ll_pool_hash_holder_left, "setBackgroundResource", R.color.bd_white);
            }

            try {

                if (intent.getAction().equals(ACTION_CLICK) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
                    remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
                    Intent i = new Intent(context, UpdateService.class);
                    i.putExtra(UpdateService.PARAM_GET, UpdateService.GET_PROFILE);
                    context.startService(i);
                } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                    Profile profile = intent.getParcelableExtra(PARAM_PROFILE);

                    float confirmed = Float.valueOf(profile.getConfirmed_reward());
                    remoteViews.setTextViewText(R.id.txt_widget_value, App.formatReward(confirmed));
                    remoteViews.setTextColor(R.id.txt_widget_value, context.getResources().getColor(R.color.bd_green));
                    remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_confirmed_reward));

                    remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);

                } else if (intent.getAction().equals(LOADING_FAILED)) {
                    remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                }

                ComponentName widget = new ComponentName(context, ConfirmedRewardWidgetProvider.class);
                AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

            } catch (NullPointerException e) {
                remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                Log.e(getClass().getSimpleName(), "Something was null, damn! (NullPointer)");
            }

            Intent intent = new Intent(context, ConfirmedRewardWidgetProvider.class);
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
        ComponentName thisWidget = new ComponentName(context, ConfirmedRewardWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        onUpdate(context, appWidgetManager, allWidgetIds);

    }

}
