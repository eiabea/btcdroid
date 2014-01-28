package com.eiabea.btcdroid.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.PricesBitStamp;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class HttpWorker {
	public static final int PRICE_SOURCE_BITSTAMP = 0;
	public static final int PRICE_SOURCE_MTGOX = 1;

	public static final String BASEURL = "https://mining.bitcoin.cz/";
	public static final String PRICES_URL_MTGOX = "http://data.mtgox.com/api/2/BTCUSD/money/ticker_fast";
	public static final String PRICES_URL_BITSTAMP = "https://www.bitstamp.net/api/ticker/";

	public static final String STATS_URL = BASEURL + "stats/json/";
	public static final String PROFILE_URL = BASEURL + "accounts/profile/json/";

	private Context context;

	private String token;

	public static RequestQueue mQueue;

	public HttpWorkerInterface httpWorkerInterface;

	public HttpWorker() {
	}

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

		String url = HttpWorker.PROFILE_URL + "wrong" + token;

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
		Log.d(getClass().getSimpleName(), "get Prices MtGox");

		String url = HttpWorker.PRICES_URL_MTGOX;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesMtGox>(url, PricesMtGox.class, null, success, error));

	}

	public void getPricesBitStamp(Response.Listener<PricesBitStamp> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices BitStamp");

		String url = HttpWorker.PRICES_URL_BITSTAMP;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesBitStamp>(url, PricesBitStamp.class, null, success, error));

	}

	public void reload() {
		getProfile();

		getStats();

		if (App.isPriceEnabled) {
			getPrices();
		}else{
			httpWorkerInterface.onPricesLoaded(null);
		}
	}

	private void getProfile() {
		Log.d(getClass().getSimpleName(), "Getting Profile");

		getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {

				httpWorkerInterface.onProfileLoaded(profile);

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				httpWorkerInterface.onProfileError();

			}
		});
	}

	private void getPrices() {

		Log.d(getClass().getSimpleName(), "Getting Prices");

		int source = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("price_source_preference", "0"));

		switch (source) {
		case PRICE_SOURCE_BITSTAMP:
			getPricesBitStamp(new Listener<PricesBitStamp>() {

				@Override
				public void onResponse(PricesBitStamp prices) {

					GenericPrice generic = new GenericPrice();

					generic.setValueFloat(Float.parseFloat(prices.getLast()));

					httpWorkerInterface.onPricesLoaded(generic);
				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

					httpWorkerInterface.onPricesError();

				}
			});
			break;
		case PRICE_SOURCE_MTGOX:

			getPricesMtGox(new Listener<PricesMtGox>() {

				@Override
				public void onResponse(PricesMtGox prices) {

					// Write jsonData to PricesMtGox Object
					prices = App.parsePrices(prices.getData());

					httpWorkerInterface.onPricesLoaded(prices.getLastPrice());
				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

					httpWorkerInterface.onPricesError();

				}
			});
			break;

		default:
			break;
		}

	}

	private void getStats() {
		Log.d(getClass().getSimpleName(), "Getting Stats");

		App.getInstance().httpWorker.getStats(new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {
				httpWorkerInterface.onStatsLoaded(stats);

			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				httpWorkerInterface.onStatsError();

			}
		});
	}

	public void setHttpWorkerInterface(HttpWorkerInterface httpWorkerInterface) {
		this.httpWorkerInterface = httpWorkerInterface;
	}

	public interface HttpWorkerInterface {
		public void onProfileLoaded(Profile profile);
		public void onProfileError();

		public void onStatsLoaded(Stats stats);
		public void onStatsError();

		public void onPricesLoaded(GenericPrice price);
		public void onPricesError();
	};

}
