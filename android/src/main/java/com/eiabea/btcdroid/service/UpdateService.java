package com.eiabea.btcdroid.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.PricesBTCe;
import com.eiabea.btcdroid.model.PricesBitStamp;
import com.eiabea.btcdroid.model.PricesCoinDesk;
import com.eiabea.btcdroid.model.PricesCoinbase;
import com.eiabea.btcdroid.model.PricesCoinfinity;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.TimeTillPayout;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.GsonRequest;
import com.eiabea.btcdroid.util.HttpWorker;

import java.text.ParseException;
import java.util.Calendar;

public class UpdateService extends Service {

    private SharedPreferences pref;

    public static final String PARAM_GET = "param_get";

    private static final int DROP_NOTIFICATION_ID = 1566789;
    private static final int NEW_ROUND_NOTIFICATION_ID = 3219876;

    public static final int GET_PRICE = 0;
    public static final int GET_STATS = 1;
    public static final int GET_PROFILE = 2;

    private static final int PRICE_SOURCE_BITSTAMP_USD = 0;
    private static final int PRICE_SOURCE_BTCE_USD = 3;
    private static final int PRICE_SOURCE_BTCE_EUR = 4;
    private static final int PRICE_SOURCE_COINDESK_USD = 5;
    private static final int PRICE_SOURCE_COINDESK_EUR = 6;
    private static final int PRICE_SOURCE_COINDESK_GBP = 7;
    private static final int PRICE_SOURCE_COINBASE = 8;
    private static final int PRICE_SOURCE_COINFINITY_BASE = 9;
    private static final int PRICE_SOURCE_COINFINITY_ATM = 10;
    private static final int PRICE_SOURCE_COINFINITY_BITCOINBON = 11;

    private static int dropNotificationCount = 0;
    private static int newRoundNotificationCount = 0;

    private static UpdateService me;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        me = this;

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d(getClass().getSimpleName(), "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        try {
            int get = intent.getIntExtra(PARAM_GET, -1);

            switch (get) {
                case GET_PROFILE:
                    getProfileWidgets();
                    break;
                case GET_STATS:
                    getStatsWidgets();
                    break;
                case GET_PRICE:
                    getPriceWidgets();
                    break;

                default:
                    getProfileWidgets();
                    getPriceWidgets();
                    getStatsWidgets();
//                    getAvgLuckWidgets();
                    break;
            }

        } catch (NullPointerException e) {
            Log.d(getClass().getSimpleName(), "Start Service without data");
        }
        return START_NOT_STICKY;
    }

    public static UpdateService getInstance() {
        return me;
    }

    private void getProfileWidgets() {
        Log.d(getClass().getSimpleName(), "get Profile Widgets");

        String url = HttpWorker.PROFILE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

        HttpWorker.mQueue.add(new GsonRequest<>(url, Profile.class, new Listener<Profile>() {

            @Override
            public void onResponse(Profile profile) {
                Log.d(getClass().getSimpleName(), "onResponse Profile Widgets");
                handleDropNotification(profile);
                onProfileLoaded(profile);
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getSimpleName(), "onErrorResponse Profile Widgets");
                Log.d(getClass().getSimpleName(), " " + error.getCause());
                onProfileError();
            }
        }));

    }

    private void getStatsWidgets() {
        Log.d(getClass().getSimpleName(), "get Stats Widgets");

        String url = HttpWorker.STATS_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

        HttpWorker.mQueue.add(new GsonRequest<>(url, Stats.class, new Listener<Stats>() {

            @Override
            public void onResponse(Stats stats) {
                Log.d(getClass().getSimpleName(), "onResponse Stats Widgets");
                handleRoundFinishedNotification(stats);
                onStatsLoaded(stats);
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getSimpleName(), "onErrorResponse Stats Widgets");
                Log.d(getClass().getSimpleName(), " " + error.getCause());
                onStatsError();
            }
        }));
    }

    private void getPriceWidgets() {
        Log.d(getClass().getSimpleName(), "get Price Widgets");

        Log.d(getClass().getSimpleName(), "Getting Prices");

        final int source = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("price_source_preference", "0"));

        switch (source) {
            case PRICE_SOURCE_BITSTAMP_USD:
                App.getInstance().httpWorker.getPricesBitStamp(new Listener<PricesBitStamp>() {

                    @Override
                    public void onResponse(PricesBitStamp prices) {

                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(Float.parseFloat(prices.getLast()));
                            price.setSource(getApplicationContext().getString(R.string.BitStamp_short));
                            price.setSymbol("$");

                            onPriceLoaded(price);
                        } catch (NullPointerException ignore) {
                            onPriceError();
                        }

                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();

                    }
                });
                break;
            case PRICE_SOURCE_COINBASE:
                App.getInstance().httpWorker.getPricesCoinbase(new Listener<PricesCoinbase>() {

                    @Override
                    public void onResponse(PricesCoinbase prices) {

                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(prices.getSubtotal().getAmount());
                            price.setSource(getApplicationContext().getString(R.string.Coinbase_short));
                            price.setSymbol("$");

                            onPriceLoaded(price);
                        } catch (NullPointerException ignore) {
                            onPriceError();
                        }

                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;
            case PRICE_SOURCE_BTCE_USD:

                App.getInstance().httpWorker.getPricesBTCe("USD", new Listener<PricesBTCe>() {

                    @Override
                    public void onResponse(PricesBTCe prices) {
                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(prices.getTicker().getLast());
                            price.setSource(getApplicationContext().getString(R.string.BTCe_short));
                            price.setSymbol("$");

                            onPriceLoaded(price);
                        } catch (NullPointerException e) {
                            onPriceError();
                        }
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;
            case PRICE_SOURCE_BTCE_EUR:

                App.getInstance().httpWorker.getPricesBTCe("EUR", new Listener<PricesBTCe>() {

                    @Override
                    public void onResponse(PricesBTCe prices) {

                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(prices.getTicker().getLast());
                            price.setSource(getApplicationContext().getString(R.string.BTCe_short));
                            price.setSymbol("€");

                            onPriceLoaded(price);
                        } catch (NullPointerException e) {
                            onPriceError();
                        }
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;
            case PRICE_SOURCE_COINDESK_EUR:

                App.getInstance().httpWorker.getPricesCoinDesk("EUR", new Listener<PricesCoinDesk>() {

                    @Override
                    public void onResponse(PricesCoinDesk prices) {

                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(prices.getBpi().getEUR().getRate_float());
                            price.setSource(getApplicationContext().getString(R.string.CoinDesk_short));
                            price.setSymbol("€");

                            onPriceLoaded(price);
                        } catch (NullPointerException e) {
                            onPriceError();
                        }
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;
            case PRICE_SOURCE_COINDESK_USD:

                App.getInstance().httpWorker.getPricesCoinDesk("USD", new Listener<PricesCoinDesk>() {

                    @Override
                    public void onResponse(PricesCoinDesk prices) {

                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(prices.getBpi().getUSD().getRate_float());
                            price.setSource(getApplicationContext().getString(R.string.CoinDesk_short));
                            price.setSymbol("$");

                            onPriceLoaded(price);
                        } catch (NullPointerException e) {
                            onPriceError();
                        }
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;
            case PRICE_SOURCE_COINDESK_GBP:

                App.getInstance().httpWorker.getPricesCoinDesk("GBP", new Listener<PricesCoinDesk>() {

                    @Override
                    public void onResponse(PricesCoinDesk prices) {

                        try {
                            GenericPrice price = new GenericPrice();
                            price.setValueFloat(prices.getBpi().getGBP().getRate_float());
                            price.setSource(getApplicationContext().getString(R.string.CoinDesk_short));
                            price.setSymbol("£");

                            onPriceLoaded(price);
                        } catch (NullPointerException e) {
                            onPriceError();
                        }
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;
            case PRICE_SOURCE_COINFINITY_BASE:
            case PRICE_SOURCE_COINFINITY_ATM:
            case PRICE_SOURCE_COINFINITY_BITCOINBON:

                App.getInstance().httpWorker.getPricesCoinfinity(new Listener<PricesCoinfinity>() {

                    @Override
                    public void onResponse(PricesCoinfinity prices) {

                        try {
                            GenericPrice price = new GenericPrice();

                            switch (source) {
                                case PRICE_SOURCE_COINFINITY_BASE:
                                    price.setValueFloat(prices.getBase());
                                    price.setSource(getApplicationContext().getString(R.string.CoinfinityBase_short));
                                    break;
                                case PRICE_SOURCE_COINFINITY_ATM:
                                    price.setValueFloat(prices.getAtm());
                                    price.setSource(getApplicationContext().getString(R.string.CoinfinityAtm_short));
                                    break;
                                case PRICE_SOURCE_COINFINITY_BITCOINBON:
                                    price.setValueFloat(prices.getBitcoinbon());
                                    price.setSource(getApplicationContext().getString(R.string.CoinfinityBitcoinbon_short));
                                    break;
                            }

                            price.setSymbol("€");

                            onPriceLoaded(price);
                        } catch (NullPointerException e) {
                            onPriceError();
                        }
                    }

                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        onPriceError();
                    }
                });
                break;

            default:
                break;
        }
    }

    private void handleRoundFinishedNotification(Stats stats) {
        boolean globalEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notification_enabled", false);
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("round_finished_notification_enabled", false);

        // Debug
//		createRoundFinishedNotification();

        if (enabled && globalEnabled) {
            try {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                long lastDuration = pref.getLong("lastDuration", 0);
                long duration = App.dateDurationFormat.parse(stats.getRound_duration()).getTime();
                if (lastDuration > duration) {
                    createRoundFinishedNotification();
                }
                pref.edit().putLong("lastDuration", duration).apply();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    private void createRoundFinishedNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.ACTION_NEW_ROUND_NOTIFICATION);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_maps_local_atm)
                .setContentTitle(getString(R.string.txt_new_round_title))
                .setContentText(getString(R.string.txt_new_round_message))
                .setAutoCancel(true);

        mBuilder.setDeleteIntent(getDeleteIntent(OnDeleteReceiver.ACTION_DELETE_NEW_ROUND));
        mBuilder.setWhen(Calendar.getInstance().getTimeInMillis());
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_round_finished));
        mBuilder.setNumber(++newRoundNotificationCount);
        mBuilder.setContentIntent(pi);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = setFinishParameters(mBuilder.build());

        mNotificationManager.notify(NEW_ROUND_NOTIFICATION_ID, notif);

        NotificationManagerCompat.from(this).notify(NEW_ROUND_NOTIFICATION_ID, mBuilder.build());

    }

    private void handleDropNotification(Profile profile) {
        boolean enabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notification_enabled", false);
        try {

            if (enabled) {

                long totalHashrate = 0;
                long limit = Integer.valueOf(pref.getString("notification_hashrate", "0"));

                if (profile != null) {
                    for (Worker tmpWorker : profile.getWorkersList()) {
                        totalHashrate += tmpWorker.getHashrate();
                    }
                } else {
                    totalHashrate = App.getTotalHashrate(getApplicationContext());
                }

                if (limit > 0 && totalHashrate < limit) {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setAction(MainActivity.ACTION_DROP_NOTIFICATION);
                    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    mBuilder.setContentTitle(getString(R.string.txt_hashrate_dropped_title));
                    mBuilder.setSmallIcon(R.drawable.ic_stat_alert_warning);
                    mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                    mBuilder.setContentText(String.format(getString(R.string.txt_hashrate_dropped_message), App.formatHashRate(totalHashrate)));
                    mBuilder.setAutoCancel(true);
                    mBuilder.setWhen(Calendar.getInstance().getTimeInMillis());
                    mBuilder.setNumber(++dropNotificationCount);
                    mBuilder.setContentIntent(pi);
                    mBuilder.setDeleteIntent(getDeleteIntent(OnDeleteReceiver.ACTION_DELETE_DROP));
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notif = setDefaults(mBuilder.build());

                    mNotificationManager.notify(DROP_NOTIFICATION_ID, notif);

                }
            }
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "Something was null, damn! (NullPointer)");
        }

    }

    private PendingIntent getDeleteIntent(String action) {
        Intent deleteIntent = new Intent(this, OnDeleteReceiver.class);
        deleteIntent.setAction(action);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);
    }

    private Notification setDefaults(Notification notification) {
        boolean sound = pref.getBoolean("notification_sound", true);
        boolean vibrate = pref.getBoolean("notification_vibrate", false);
        boolean led = pref.getBoolean("notification_led", false);

        if (sound) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }

        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }

        if (led) {
            notification.ledARGB = 0xffff8b00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

        return notification;
    }

    private Notification setFinishParameters(Notification notification) {
        boolean sound = pref.getBoolean("notification_sound", true);
        boolean vibrate = pref.getBoolean("notification_vibrate", false);
        boolean led = pref.getBoolean("notification_led", false);

        if (sound) {
            notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tada);
        }

        if (vibrate) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (led) {
            notification.ledARGB = 0xffff8b00;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

        return notification;
    }

    public static void resetDropNotificationCount() {
        dropNotificationCount = 0;
    }

    public static void resetNewRoundNotificationCount() {
        newRoundNotificationCount = 0;
    }

    private void onPriceLoaded(GenericPrice price) {

        DataProvider.insertOrUpdatePrice(getApplicationContext(), price);

        App.updateWidgets(getApplicationContext());

    }

    private void onPriceError() {
        App.updateWidgets(getApplicationContext());
    }

    private void onProfileLoaded(Profile profile) {

        DataProvider.insertOrUpdateTimeTillPayout(getApplicationContext(), new TimeTillPayout(profile));

        DataProvider.insertOrUpdateProfile(getApplicationContext(), profile);

        DataProvider.insertOrUpdateWorkers(getApplicationContext(), profile.getWorkers());

        App.updateWidgets(getApplicationContext());
    }

    private void onProfileError() {
        App.updateWidgets(getApplicationContext());
    }

    private void onStatsLoaded(Stats stats) {

        DataProvider.insertOrUpdateStats(getApplicationContext(), stats);

        DataProvider.insertOrUpdateRounds(getApplicationContext(), stats.getBlocks());

        App.updateWidgets(getApplicationContext());
    }

    private void onStatsError() {
        App.updateWidgets(getApplicationContext());
    }

}
