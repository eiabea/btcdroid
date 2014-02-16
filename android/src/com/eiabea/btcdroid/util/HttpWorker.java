package com.eiabea.btcdroid.util;

import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.eiabea.btcdroid.model.PricesBTCe;
import com.eiabea.btcdroid.model.PricesBitStamp;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class HttpWorker {
	public static final String BASEURL = "https://mining.bitcoin.cz/";
	public static final String PRICES_URL_MTGOX_FRONT = "http://data.mtgox.com/api/2/BTC";
	public static final String PRICES_URL_MTGOX_END = "/money/ticker_fast";
	public static final String PRICES_URL_BTCE_FRONT = "https://btc-e.com/api/2/btc_";
	public static final String PRICES_URL_BTCE_END = "/ticker";
	public static final String PRICES_URL_BITSTAMP = "https://www.bitstamp.net/api/ticker/";

	public static final String STATS_URL = BASEURL + "stats/json/";
	public static final String PROFILE_URL = BASEURL + "accounts/profile/json/";

	private static HttpWorker me;

	private Context context;

	private String token;

	public static RequestQueue mQueue;

	public HttpWorker() {
	}

	public HttpWorker(Context context, String token) {
		HttpWorker.me = this;
		this.context = context;

		this.token = token;

		initVolley();
	}

	public static HttpWorker getInstance() {
		return me;
	}

	private void initVolley() {
		mQueue = Volley.newRequestQueue(context);
		mQueue.start();
	}

	public void getProfile(Response.Listener<Profile> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Profile");

		String url = HttpWorker.PROFILE_URL + token;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, success, error));

	}

	public void getStats(Response.Listener<Stats> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Stats");

		String url = HttpWorker.STATS_URL + token;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Stats>(url, Stats.class, null, success, error));

	}

	public void getPricesMtGox(String currency, Response.Listener<PricesMtGox> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices MtGox");

		String url = HttpWorker.PRICES_URL_MTGOX_FRONT + currency + HttpWorker.PRICES_URL_MTGOX_END;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesMtGox>(url, PricesMtGox.class, null, success, error));

	}

	public void getPricesBTCe(String currency, Response.Listener<PricesBTCe> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices BTC-e");

		String url = HttpWorker.PRICES_URL_BTCE_FRONT + currency.toLowerCase(Locale.ENGLISH) + HttpWorker.PRICES_URL_BTCE_END;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesBTCe>(url, PricesBTCe.class, null, success, error));

	}

	public void getPricesBitStamp(Response.Listener<PricesBitStamp> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices BitStamp");

		String url = HttpWorker.PRICES_URL_BITSTAMP;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesBitStamp>(url, PricesBitStamp.class, null, success, error));

	}

}
