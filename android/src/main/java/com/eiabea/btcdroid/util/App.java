package com.eiabea.btcdroid.util;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

public class App extends Application {

    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat dateStatsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat dateDurationFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    public static final String PREF_TOKEN = "token";

    public static boolean isPriceEnabled = true;

    private static String token = "";
    private int luckThreshold = 15;
    private int priceThreshold = 15;

    public HttpWorker httpWorker;

    public Gson gson;

    private SharedPreferences pref;

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
        gson = new Gson();

        initPrefs();

        me = this;
        httpWorker = new HttpWorker(this.getApplicationContext(), token);

    }

    private void initPrefs() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);

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

    public void setToken(String token) {
        App.token = token;

        httpWorker = new HttpWorker(this.getApplicationContext(), token);

    }

    public static String getToken() {
        return token;
    }

    public boolean isTokenSet() {
        if (token != null && token.length() > 0) {
            return true;
        }
        return false;
    }

    public static List<Block> parseBlocks(JsonObject json) {
        List<Block> blocks = new ArrayList<Block>();

        Set<Entry<String, JsonElement>> set = json.entrySet();

        for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext(); ) {
            Entry<String, JsonElement> current = it.next();

            blocks.add(App.getInstance().gson.fromJson(current.getValue(), Block.class));

        }
        return blocks;
    }

    public static String formatHashRate(String hash) {
        double doubleHash = Double.valueOf(hash);
        if (doubleHash > 1000) {
            return String.format("%.2f", doubleHash / 1000d) + " " + getResString(R.string.gh_per_second, getInstance());
        }
        return doubleHash + " " + getResString(R.string.mh_per_second, getInstance());
    }

    public static String formatHashRate(int hash) {
        if (hash > 1000000) {
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
                return String.format("%.6f", reward) + getResString(R.string.btc_short, getInstance());
            case 1:
                return String.format("%.2f", reward * 1000) + getResString(R.string.mbtc_short, getInstance());
            case 2:
                return String.format("%.2f", reward * 1000 * 1000) + getResString(R.string.ubtc_short, getInstance());

            default:
                return String.format("%.6f", reward) + getResString(R.string.btc_short, getInstance());
        }

    }

    public static String formatPrice(String symbol, float price) {
        return symbol + " " + String.format("%.2f", price);
    }

    public static String getResString(int id, Context ctx) {
        return ctx.getResources().getString(id);
    }

    public static int getTotalHashrate(Profile profile) {
        ArrayList<Worker> list = profile.getWorkersList();

        int totalHashrate = 0;

        for (Worker tmp : list) {
            totalHashrate += tmp.getHashrate();
        }
        return totalHashrate;
    }

    public static class WorkerSorter implements Comparator<Worker> {

        @Override
        public int compare(Worker lhs, Worker rhs) {
            return (lhs.isAlive() ^ rhs.isAlive()) ? ((lhs.isAlive() ^ true) ? 1 : -1) : 0;
        }

    }

    public static class BlockSorter implements Comparator<Block> {

        @Override
        public int compare(Block lhs, Block rhs) {

            try {
                long timestampLhs = dateStatsFormat.parse(lhs.getDate_found()).getTime();
                long timestampRhs = dateStatsFormat.parse(rhs.getDate_found()).getTime();
                if (timestampLhs < timestampRhs) return 1;
                if (timestampLhs > timestampRhs) return -1;
                return 0;
            } catch (ParseException e) {
                Log.e(getClass().getSimpleName(), "Can't get sort blocks per DateFound (ParseExecption)");
            }

            return 0;
        }

    }

    public static void updateWidgets(Context context, Profile profile) {
        // Update Total Widget
        Intent totalIntent = new Intent(context, TotalHashrateWidgetProvider.class);
        totalIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        totalIntent.putExtra(TotalHashrateWidgetProvider.PARAM_PROFILE, profile);
        context.sendBroadcast(totalIntent);
        // Update Total Widget
        Intent averageIntent = new Intent(context, AverageHashrateWidgetProvider.class);
        averageIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        averageIntent.putExtra(TotalHashrateWidgetProvider.PARAM_PROFILE, profile);
        context.sendBroadcast(averageIntent);
        // Update Confirmed Reward Widget
        Intent confirmedIntent = new Intent(context, ConfirmedRewardWidgetProvider.class);
        confirmedIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        confirmedIntent.putExtra(TotalHashrateWidgetProvider.PARAM_PROFILE, profile);
        context.sendBroadcast(confirmedIntent);
        // Update Estimated Reward Widget
        Intent estimatedIntent = new Intent(context, EstimatedRewardWidgetProvider.class);
        estimatedIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        estimatedIntent.putExtra(TotalHashrateWidgetProvider.PARAM_PROFILE, profile);
        context.sendBroadcast(estimatedIntent);
        // Update Total Reward Widget
        Intent totalRewardIntent = new Intent(context, TotalRewardWidgetProvider.class);
        totalRewardIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        totalRewardIntent.putExtra(TotalHashrateWidgetProvider.PARAM_PROFILE, profile);
        context.sendBroadcast(totalRewardIntent);
        // Update Multi Widget
        Intent multiIntent = new Intent(context, MultiWidgetProvider.class);
        multiIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        multiIntent.putExtra(MultiWidgetProvider.PARAM_PROFILE, profile);
        context.sendBroadcast(multiIntent);

        // Update Dashclock
        Intent dashclockIntent = new Intent(DashClockWidget.UPDATE_DASHCLOCK);
//        dashclockIntent.putExtra(TotalHashrateWidgetProvider.PARAM_PROFILE, profile);
        LocalBroadcastManager.getInstance(context).sendBroadcast(dashclockIntent);
        Log.d(context.getClass().getSimpleName(), "sent Broadcast to update DashClock");
    }

    public static void updateWidgets(Context context, Stats stats) {
        // Update Round Widget
        Intent roundIntent = new Intent(context, RoundDurationWidgetProvider.class);
        roundIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        roundIntent.putExtra(RoundDurationWidgetProvider.PARAM_STATS, stats);
        context.sendBroadcast(roundIntent);
        // Update Multi Widget
        Intent multiIntent = new Intent(context, MultiWidgetProvider.class);
        multiIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        multiIntent.putExtra(MultiWidgetProvider.PARAM_STATS, stats);
        context.sendBroadcast(multiIntent);
    }

    public static void updateWidgets(Context context, GenericPrice price) {
        // Update Price Widget
        Intent priceIntent = new Intent(context, PriceWidgetProvider.class);
        priceIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        priceIntent.putExtra(PriceWidgetProvider.PARAM_PRICE, price);
        context.sendBroadcast(priceIntent);
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
