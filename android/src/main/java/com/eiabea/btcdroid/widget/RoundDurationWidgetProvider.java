package com.eiabea.btcdroid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.util.App;

import java.text.ParseException;
import java.util.Date;

public class RoundDurationWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK";
    public static final String LOADING_FAILED = "ACTION_FAILED";

    private Intent intent;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean trans = pref.getBoolean("transparent_widgets", false);

        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            if(trans){
                remoteViews.setInt(R.id.ll_pool_hash_holder_left, "setBackgroundResource", R.color.bd_black_transparent);
            }else{
                remoteViews.setInt(R.id.ll_pool_hash_holder_left, "setBackgroundResource", R.color.bd_white);
            }

            try {

                if (intent.getAction().equals(ACTION_CLICK) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
                    int behavior = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("widget_behavior_preference", "0"));

                    Intent i;

                    switch (behavior){
                        case 0:
                            remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
                            i = new Intent(context, UpdateService.class);
                            i.putExtra(UpdateService.PARAM_GET, UpdateService.GET_STATS);
                            context.startService(i);
                            break;
                        case 1:
                            i = new Intent(context, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            break;
                    }
                } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
                    String selectionStats = Stats._ID + "=?";
                    String[] selectionArgsStats = { "1" };

                    Cursor cStats = context.getContentResolver().query(Stats.CONTENT_URI, null,selectionStats,selectionArgsStats, null);

                    if (cStats.getCount() > 0) {
                        cStats.moveToFirst();

                        Stats stats = new Stats(cStats);
                        stats = App.getInstance().gson.fromJson(stats.getJson(), Stats.class);

                        Date duration;

                        try {
                            duration = App.dateDurationFormat.parse(stats.getRound_duration());
                            remoteViews.setTextViewText(R.id.txt_widget_value, App.dateDurationFormat.format(duration));
                            remoteViews.setTextColor(R.id.txt_widget_value, ContextCompat.getColor(context, R.color.bd_green));
                            remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_round_duration_widget));
                        } catch (ParseException e) {
                            Log.e(getClass().getSimpleName(), "Can't parse RoundDuration (ParseExeception)");
                            remoteViews.setTextViewText(R.id.txt_widget_value, context.getString(R.string.txt_greater_one_day));
                            remoteViews.setTextColor(R.id.txt_widget_value, ContextCompat.getColor(context, R.color.bd_green));
                            remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_round_duration_widget));
                        }

                        remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                    }

                    cStats.close();

                } else if (intent.getAction().equals(LOADING_FAILED)) {
                    remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                }

                ComponentName widget = new ComponentName(context, RoundDurationWidgetProvider.class);
                AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

            } catch (NullPointerException e) {
                remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                Log.e(getClass().getSimpleName(), "Something was null, damn! (NullPointer)");
            }

            Intent intent = new Intent(context, RoundDurationWidgetProvider.class);
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
        ComponentName thisWidget = new ComponentName(context, RoundDurationWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        onUpdate(context, appWidgetManager, allWidgetIds);

    }

}
