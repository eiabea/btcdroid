package com.eiabea.btcdroid.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.util.App;

public class TotalHashrateWidgetProvider extends AppWidgetProvider{
    private static final String ACTION_CLICK = "ACTION_CLICK";
    public static final String LOADING_FAILED = "ACTION_FAILED";

    private Intent intent;

//    /**
//     * Our data observer just notifies an update for all weather widgets when it detects a change.
//     */
//    class WeatherDataProviderObserver extends ContentObserver {
//        private AppWidgetManager mAppWidgetManager;
//        private ComponentName mComponentName;
//
//        WeatherDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h) {
//            super(h);
//            mAppWidgetManager = mgr;
//            mComponentName = cn;
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            // The data has changed, so notify the widget that the collection view needs to be updated.
//            // In response, the factory's onDataSetChanged() will be called which will requery the
//            // cursor for the new data.
//            mAppWidgetManager.notifyAppWidgetViewDataChanged(
//                    mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.txt_widget_value);
//        }
//    }
//
//    private static HandlerThread sWorkerThread;
//    private static Handler sWorkerQueue;
//    private static WeatherDataProviderObserver sDataObserver;
//
//    public TotalHashrateWidgetProvider() {
//        // Start the worker thread
//        sWorkerThread = new HandlerThread("TotalHashrateWidgetProvider-worker");
//        sWorkerThread.start();
//        sWorkerQueue = new Handler(sWorkerThread.getLooper());
//    }
//
//    @Override
//    public void onEnabled(Context context) {
//        // Register for external updates to the data to trigger an update of the widget.  When using
//        // content providers, the data is often updated via a background service, or in response to
//        // user interaction in the main app.  To ensure that the widget always reflects the current
//        // state of the data, we must listen for changes and update ourselves accordingly.
//        final ContentResolver r = context.getContentResolver();
//        if (sDataObserver == null) {
//            final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//            final ComponentName cn = new ComponentName(context, TotalHashrateWidgetProvider.class);
//            sDataObserver = new WeatherDataProviderObserver(mgr, cn, sWorkerQueue);
//            r.registerContentObserver(Worker.CONTENT_URI, true, sDataObserver);
//        }
//    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean trans = pref.getBoolean("transparent_widgets", false);

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, TotalHashrateWidgetProvider.class);
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

                    switch (behavior){
                        case 0:
                            remoteViews.setViewVisibility(R.id.fl_widget_loading, View.VISIBLE);
                            i = new Intent(context, UpdateService.class);
                            i.putExtra(UpdateService.PARAM_GET, UpdateService.GET_API_RESPONSE);
                            context.startService(i);
                            break;
                        case 1:
                            i = new Intent(context, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(i);
                            break;
                    }
                } else if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {

                    remoteViews.setTextViewText(R.id.txt_widget_value, App.formatHashRate(App.getTotalHashrate(context)));
                    remoteViews.setTextColor(R.id.txt_widget_value, context.getResources().getColor(R.color.bd_green));
                    remoteViews.setTextViewText(R.id.txt_widget_desc, context.getString(R.string.txt_current_total_hashrate));

                    remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);

                } else if (intent.getAction().equals(LOADING_FAILED)) {
                    remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                }

                ComponentName widget = new ComponentName(context, TotalHashrateWidgetProvider.class);
                AppWidgetManager.getInstance(context).updateAppWidget(widget, remoteViews);

            } catch (NullPointerException e) {
                remoteViews.setViewVisibility(R.id.fl_widget_loading, View.GONE);
                Log.e(getClass().getSimpleName(), "Something was null, damn! (NullPointer)");
            }

            Intent intent = new Intent(context, TotalHashrateWidgetProvider.class);
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
        ComponentName thisWidget = new ComponentName(context, TotalHashrateWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        onUpdate(context, appWidgetManager, allWidgetIds);

    }
}
