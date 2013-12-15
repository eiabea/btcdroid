package com.eiabea.btcdroid.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class HttpWorker {

	public static final String BASEURL = "https://mining.bitcoin.cz/";
	public static final String PRICES_URL = "http://data.mtgox.com/api/2/BTCUSD/money/ticker_fast";
	public static final String PRICES_URL_BITSTAMP = "https://www.bitstamp.net/api/ticker/";

	public static final String STATS_URL = BASEURL + "stats/json/";
	public static final String PROFILE_URL = BASEURL + "accounts/profile/json/";

	private Context context;
	
	private String token;

	public static RequestQueue mQueue;

	public HttpWorker() {}

	public HttpWorker(Context context, String token) {
		this.context = context;
		
		this.token = token;
		
		initVolley();
	}

	private void initVolley() {
		mQueue = Volley.newRequestQueue(context);
		mQueue.start();
	}

	public void getProfile(Response.Listener<Profile> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Profile");

		String url = HttpWorker.PROFILE_URL + token ;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, success, error));

	}

	public void getStats(Response.Listener<Stats> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Stats");

		String url = HttpWorker.STATS_URL + token;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Stats>(url, Stats.class, null, success, error));

	}

	public void getPricesMtGox(Response.Listener<PricesMtGox> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices");

		String url = HttpWorker.PRICES_URL;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesMtGox>(url, PricesMtGox.class, null, success, error));

	}

}
