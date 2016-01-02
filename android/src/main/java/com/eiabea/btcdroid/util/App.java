package com.eiabea.btcdroid.util;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.widget.AverageHashrateWidgetProvider;
import com.eiabea.btcdroid.widget.ConfirmedRewardWidgetProvider;
import com.eiabea.btcdroid.widget.DashClockWidget;
import com.eiabea.btcdroid.widget.EstimatedRewardWidgetProvider;
import com.eiabea.btcdroid.widget.MultiWidgetProvider;
import com.eiabea.btcdroid.widget.PriceWidgetProvider;
import com.eiabea.btcdroid.widget.RoundDurationWidgetProvider;
import com.eiabea.btcdroid.widget.TotalHashrateWidgetProvider;
import com.eiabea.btcdroid.widget.TotalRewardWidgetProvider;
import com.google.gson.Gson;

import net.danlew.android.joda.JodaTimeAndroid;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class App extends Application {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat dateStatsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat dateDurationFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//    public static final SimpleDateFormat dateDurationDayFormat = new SimpleDateFormat("dd HH:mm:ss", Locale.getDefault());

    public static final String PREF_TOKEN = "token";

    public static boolean isPriceEnabled = true;

    private static String token = "";
    private int luckThreshold = 15;
    private int priceThreshold = 15;

    private static ArrayList<Class> allWidgetClasses;

    public HttpWorker httpWorker;

    public Gson gson;

    /**
     * Object of own Class
     */
    private static App me;

    /**
     * Initialize itself and all needed Helper
     */
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        gson = new Gson();

        allWidgetClasses = new ArrayList<>();
        allWidgetClasses.add(TotalHashrateWidgetProvider.class);
        allWidgetClasses.add(AverageHashrateWidgetProvider.class);
        allWidgetClasses.add(ConfirmedRewardWidgetProvider.class);
        allWidgetClasses.add(EstimatedRewardWidgetProvider.class);
        allWidgetClasses.add(TotalRewardWidgetProvider.class);
        allWidgetClasses.add(MultiWidgetProvider.class);
        allWidgetClasses.add(RoundDurationWidgetProvider.class);
        allWidgetClasses.add(PriceWidgetProvider.class);

        initPrefs();

        me = this;
        httpWorker = new HttpWorker(this.getApplicationContext());

    }

    private void initPrefs() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        token = pref.getString(PREF_TOKEN, "");
        luckThreshold = Integer.valueOf(pref.getString("luck_threshold", "15"));
        priceThreshold = Integer.valueOf(pref.getString("price_threshold", "15"));
        isPriceEnabled = pref.getBoolean("price_enabled", false);

    }

    /**
     * @return Instance of App.class
     */
    public static App getInstance() {
        return me;
    }

    public void resetToken() {
        App.token = PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

        DataProvider.clearWorkers(getApplicationContext());

        setToken(App.token);
    }


    public void resetLuckThreshold() {
        this.luckThreshold = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("luck_threshold", "15"));
    }

    public int getLuckThreshold() {
        return this.luckThreshold;
    }

    public void resetPriceThreshold() {
        this.priceThreshold = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("price_threshold", "15"));
    }

    public void resetPriceEnabled() {
        App.isPriceEnabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("price_enabled", false);
    }

    public int getPriceThreshold() {
        return this.priceThreshold;
    }

    private void setToken(String token) {
        App.token = token;

        httpWorker = new HttpWorker(this.getApplicationContext());

    }

    public boolean isTokenSet() {
        return token != null && token.length() > 0;
    }

    public static String formatHashRate(String hash) {
        double doubleHash = Double.valueOf(hash);
        if (doubleHash > 1000) {
            return String.format("%.2f", doubleHash / 1000d) + " " + getResString(R.string.gh_per_second, getInstance());
        }
        return doubleHash + " " + getResString(R.string.mh_per_second, getInstance());
    }

    public static String formatHashRate(long hash) {
        if (hash > 1000000000) {
            return String.format("%.2f", ((float) hash) / 1000000f) + " " + getResString(R.string.ph_per_second, getInstance());
        } else if (hash > 1000000) {
            return String.format("%.2f", ((float) hash) / 1000000f) + " " + getResString(R.string.th_per_second, getInstance());
        } else if (hash > 10000) {
            return String.format("%.1f", ((float) hash) / 1000f) + " " + getResString(R.string.gh_per_second, getInstance());
        } else if (hash > 1000) {
            return String.format("%.2f", ((float) hash) / 1000f) + " " + getResString(R.string.gh_per_second, getInstance());

        }
        return String.valueOf(hash) + " " + getResString(R.string.mh_per_second, getInstance());
    }

    public static String formatProcent(float raw) {
        return String.format("%.0f", raw * 100) + " " + getResString(R.string.percent_sign, getInstance());
    }

    public static String formatProcentHighPrecision(float raw) {
        return String.format("%.2f", raw * 100) + " " + getResString(R.string.percent_sign, getInstance());
    }

    public static String formatReward(float reward) {
        int style = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getInstance()).getString("btc_style_preference", "0"));

        switch (style) {
            case 0:
                return String.format("%.6f", reward);
            case 1:
                return String.format("%.2f", reward * 1000) + getResString(R.string.mbtc_short, getInstance());
            case 2:
                return String.format("%.2f", reward * 1000 * 1000) + getResString(R.string.ubtc_short, getInstance());
            default:
                return "";
        }

    }

    public static String formatPrice(String symbol, float price) {
        return symbol + " " + String.format("%.2f", price);
    }

    public static String getResString(int id, Context ctx) {
        return ctx.getResources().getString(id);
    }

    public static long getTotalHashrate(Context context) {

        long totalHashrate = 0;
        String[] projection = new String[]{Worker.HASHRATE};

        Cursor c = context.getContentResolver().query(Worker.CONTENT_URI, projection, null, null, null);

        if (c.getCount() > 0) {
            while (c.moveToNext()) {

                int index = c.getColumnIndex(Worker.HASHRATE);
                long value = c.getLong(index);
                totalHashrate += value;
            }

        }

        c.close();

        return totalHashrate;
    }

    public static void updateWidgets(Context context) {

        for (Class c : allWidgetClasses) {
            Intent intent = new Intent(context, c);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            context.sendBroadcast(intent);
        }

        // Update Dashclock
        Intent dashclockIntent = new Intent(DashClockWidget.UPDATE_DASHCLOCK);
        LocalBroadcastManager.getInstance(context).sendBroadcast(dashclockIntent);
        Log.d(context.getClass().getSimpleName(), "sent Broadcast to update DashClock");
    }

    public static void updateData(Context context) {
        Intent i = new Intent(context, UpdateService.class);
        context.startService(i);
    }

    public static void resetUpdateManager(Context context) {
        int intervalWidget = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("update_interval", "30"));
        int intervalMilli = intervalWidget * 60 * 1000;
        Log.w("BTCDroid", "reseting UpdateManager!");
        Log.w("BTCDroid", "Interval: " + intervalWidget + "min");
        Intent i = new Intent(context, UpdateService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pi); // cancel any existing alarms
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), intervalMilli, pi);
    }

}
