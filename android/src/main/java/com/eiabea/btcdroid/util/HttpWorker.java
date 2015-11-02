package com.eiabea.btcdroid.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.eiabea.btcdroid.model.PricesBTCe;
import com.eiabea.btcdroid.model.PricesBitStamp;
import com.eiabea.btcdroid.model.PricesCoinDesk;
import com.eiabea.btcdroid.model.PricesCoinbase;
import com.eiabea.btcdroid.model.PricesCoinfinity;

import java.util.Locale;

public class HttpWorker {
    private static final String BASEURL = "https://mining.bitcoin.cz/";
    private static final String PRICES_URL_BTCE_FRONT = "https://btc-e.com/api/2/btc_";
    private static final String PRICES_URL_BTCE_END = "/ticker";
    private static final String PRICES_URL_COINDESK_FRONT = "http://api.coindesk.com/v1/bpi/currentprice/";
    private static final String PRICES_URL_COINDESK_END = ".json";
    private static final String PRICES_URL_BITSTAMP = "https://www.bitstamp.net/api/ticker/";
    private static final String PRICES_URL_COINBASE = "https://coinbase.com/api/v1/prices/buy";
    private static final String PRICES_URL_COINFINITY = "https://coinfinity.co/api/Ticker";

    public static final String STATS_URL = BASEURL + "stats/json/";
    public static final String PROFILE_URL = BASEURL + "accounts/profile/json/";

    private static HttpWorker me;

    private final Context context;

    public static RequestQueue mQueue;

    public HttpWorker(Context context) {
        HttpWorker.me = this;
        this.context = context;

        initVolley();
    }

    public static HttpWorker getInstance() {
        return me;
    }

    private void initVolley() {
        mQueue = Volley.newRequestQueue(context);
        mQueue.start();
    }

    public void getPricesBTCe(String currency, Response.Listener<PricesBTCe> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices BTC-e");

        String url = HttpWorker.PRICES_URL_BTCE_FRONT + currency.toLowerCase(Locale.ENGLISH) + HttpWorker.PRICES_URL_BTCE_END;

        HttpWorker.mQueue.add(new GsonRequest<>(url, PricesBTCe.class, success, error));

    }

    public void getPricesCoinDesk(String currency, Response.Listener<PricesCoinDesk> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices CoinDesk");

        String url = HttpWorker.PRICES_URL_COINDESK_FRONT + currency.toLowerCase(Locale.ENGLISH) + HttpWorker.PRICES_URL_COINDESK_END;

        HttpWorker.mQueue.add(new GsonRequest<>(url, PricesCoinDesk.class, success, error));

    }

    public void getPricesCoinfinity(Response.Listener<PricesCoinfinity> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices Coinfinity");

        String url = HttpWorker.PRICES_URL_COINFINITY;

        HttpWorker.mQueue.add(new GsonRequest<>(url, PricesCoinfinity
                .class, success, error));

    }

    public void getPricesBitStamp(Response.Listener<PricesBitStamp> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices BitStamp");

        String url = HttpWorker.PRICES_URL_BITSTAMP;

        HttpWorker.mQueue.add(new GsonRequest<>(url, PricesBitStamp.class, success, error));

    }

    public void getPricesCoinbase(Response.Listener<PricesCoinbase> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices BitStamp");

        String url = HttpWorker.PRICES_URL_COINBASE;

        HttpWorker.mQueue.add(new GsonRequest<>(url, PricesCoinbase.class, success, error));

    }

}
