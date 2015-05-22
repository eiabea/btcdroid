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

import java.util.Locale;

public class HttpWorker {
    public static final String BASEURL = "https://mining.bitcoin.cz/";
    public static final String PRICES_URL_BTCE_FRONT = "https://btc-e.com/api/2/btc_";
    public static final String PRICES_URL_BTCE_END = "/ticker";
    public static final String PRICES_URL_COINDESK_FRONT = "https://api.coindesk.com/v1/bpi/currentprice/";
    public static final String PRICES_URL_COINDESK_END = ".json";
    public static final String PRICES_URL_BITSTAMP = "https://www.bitstamp.net/api/ticker/";
    public static final String PRICES_URL_COINBASE = "https://coinbase.com/api/v1/prices/buy";
    public static final String AVG_LUCK_URL = "http://slush-eiabea.rhcloud.com/avg_luck";

    public static final String STATS_URL = BASEURL + "stats/json/";
    public static final String PROFILE_URL = BASEURL + "accounts/profile/json/";

    private static HttpWorker me;

    private Context context;

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

        System.out.println(HttpWorker.mQueue.toString());

        HttpWorker.mQueue.add(new GsonRequest<PricesBTCe>(url, PricesBTCe.class, null, success, error));

    }

    public void getPricesCoinDesk(String currency, Response.Listener<PricesCoinDesk> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices CoinDesk");

        String url = HttpWorker.PRICES_URL_COINDESK_FRONT + currency.toLowerCase(Locale.ENGLISH) + HttpWorker.PRICES_URL_COINDESK_END;

        System.out.println(HttpWorker.mQueue.toString());

        HttpWorker.mQueue.add(new GsonRequest<PricesCoinDesk>(url, PricesCoinDesk.class, null, success, error));

    }

    public void getPricesBitStamp(Response.Listener<PricesBitStamp> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices BitStamp");

        String url = HttpWorker.PRICES_URL_BITSTAMP;

        System.out.println(HttpWorker.mQueue.toString());

        HttpWorker.mQueue.add(new GsonRequest<PricesBitStamp>(url, PricesBitStamp.class, null, success, error));

    }

    public void getPricesCoinbase(Response.Listener<PricesCoinbase> success, Response.ErrorListener error) {
        Log.d(getClass().getSimpleName(), "get Prices BitStamp");

        String url = HttpWorker.PRICES_URL_COINBASE;

        System.out.println(HttpWorker.mQueue.toString());

        HttpWorker.mQueue.add(new GsonRequest<PricesCoinbase>(url, PricesCoinbase.class, null, success, error));

    }

}
