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
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.util.App;

public class ConfirmedRewardWidgetProvider extends AppWidgetProvider {
    private static final String ACTION_CLICK = "ACTION_CLICK";
    private static final String LOADING_FAILED = "ACTION_FAILED";

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

            if (trans) {
                remoteViews.setInt(R.id.ll_pool_hash_holder_left, "setBackgroundResource", R.color.bd_black_transparent);
            } else {
                remoteViews.setInt(R.id.ll_pool_hash_holder_left, "setBackgroundResource", R.color.bd_white);
            }

            try {

                if (intent.getAction().equals(ACTION_CLICK) || intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_ENABLED)) {
                    int behavior = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("widget_behavior_preference", "0"));

                    Intent i;

                    switch (behavior) {
                        case 0:
                            remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
                            i = new Intent(context, UpdateService.class);
                            i.putExtra(UpdateService.PARAM_GET, UpdateService.GET_PROFILE);
//                            context.startService(i);
                            ContextCompat.startForegroundService(context, i);
                            break;
                        case 1:
                            i = new Intent(context, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            break;
                    }
                } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

                    String selection = Profile._ID + "=?";
                    String[] selectionArgs = {"1"};

                    Cursor c = context.getContentResolver().query(Profile.CONTENT_URI, null, selection, selectionArgs, null);
                    if (c.getCount() > 0) {
                        c.moveToFirst();

                        Profile profile = new Profile(c);
                        profile = App.getInstance().gson.fromJson(profile.getJson(), Profile.class);

                        float confirmed = Float.valueOf(profile.getConfirmed_reward());
                        remoteViews.setTextViewText(R.id.txt_widget_value, App.formatReward(confirmed));
                        remoteViews.setTextColor(R.id.txt_widget_value, ContextCompat.getColor(context, R.color.bd_green));
                        remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_confirmed_reward));

                        remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                    }

                    c.close();
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
