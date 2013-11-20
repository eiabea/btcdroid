package com.eiabea.btcdroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class HttpWorker {

	public static final String BASEURL = "https://mining.bitcoin.cz/";
	public static final String PRICES_URL = "http://data.mtgox.com/api/2/BTCUSD/money/ticker_fast";

	public static final String DEBUG_API_KEY = "402189-0754bbdd5fa5ea39699830dd588986e5";

	public static final String STATS_URL = BASEURL + "/stats/json/" + DEBUG_API_KEY;
	public static final String PROFILE_URL = BASEURL + "/accounts/profile/json/";

	private Context context;

	private String token = "";
	
	private SharedPreferences pref;

	public static RequestQueue mQueue;

	public HttpWorker() {}

	public HttpWorker(Context context) {
		this.context = context;
		
		pref = context.getSharedPreferences("appData", Context.MODE_PRIVATE);
		
		token = pref.getString("slushKey", "");
		
		initVolley();
	}

	private void initVolley() {
		mQueue = Volley.newRequestQueue(context);
		mQueue.start();
	}

	public void setToken(String token) {
		this.token = token;
		
		pref.edit().putString("slushKey", token).commit();
		
	}

	public boolean isTokenSet() {
		if (token != null && token.length() > 0) {
			return true;
		}
		return false;
	}

	public void getProfile(Response.Listener<Profile> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Profile");

		String url = HttpWorker.PROFILE_URL + token ;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, success, error));

	}

	public void getStats(Response.Listener<Stats> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Stats");

		String url = HttpWorker.STATS_URL;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Stats>(url, Stats.class, null, success, error));

	}

	public void getPrices(Response.Listener<Prices> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices");

		String url = HttpWorker.PRICES_URL;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Prices>(url, Prices.class, null, success, error));

	}
}
