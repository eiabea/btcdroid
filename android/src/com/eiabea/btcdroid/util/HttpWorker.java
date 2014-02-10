package com.eiabea.btcdroid.util;

import java.util.Locale;

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
import com.eiabea.btcdroid.model.PricesBTCe;
import com.eiabea.btcdroid.model.PricesBitStamp;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class HttpWorker {
	public static final int PRICE_SOURCE_BITSTAMP_USD = 0;
	public static final int PRICE_SOURCE_MTGOX_USD = 1;
	public static final int PRICE_SOURCE_MTGOX_EUR = 2;
	public static final int PRICE_SOURCE_BTCE_USD = 3;
	public static final int PRICE_SOURCE_BTCE_EUR = 4;

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

	public HttpWorkerInterface httpWorkerInterface;

	public HttpWorker() {
	}

	public HttpWorker(Context context, String token) {
		HttpWorker.me = this;
		this.context = context;

		this.token = token;

		initVolley();
	}
	
	public static HttpWorker getInstance(){
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

	private void getPricesMtGox(String currency, Response.Listener<PricesMtGox> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices MtGox");

		String url = HttpWorker.PRICES_URL_MTGOX_FRONT + currency + HttpWorker.PRICES_URL_MTGOX_END;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesMtGox>(url, PricesMtGox.class, null, success, error));

	}
	
	private void getPricesBTCe(String currency, Response.Listener<PricesBTCe> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices BTC-e");
		
		String url = HttpWorker.PRICES_URL_BTCE_FRONT + currency.toLowerCase(Locale.ENGLISH) + HttpWorker.PRICES_URL_BTCE_END;
		
		System.out.println(HttpWorker.mQueue.toString());
		
		HttpWorker.mQueue.add(new GsonRequest<PricesBTCe>(url, PricesBTCe.class, null, success, error));
		
	}

	private void getPricesBitStamp(Response.Listener<PricesBitStamp> success, Response.ErrorListener error) {
		Log.d(getClass().getSimpleName(), "get Prices BitStamp");

		String url = HttpWorker.PRICES_URL_BITSTAMP;

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<PricesBitStamp>(url, PricesBitStamp.class, null, success, error));

	}

//	public void reload() {
//		getProfile();
//
//		getStats();
//
//		if (App.isPriceEnabled) {
//			getPrices();
//		}else{
//			httpWorkerInterface.onPricesLoaded(null);
//		}
//	}

//	private void getProfile() {
//		Log.d(getClass().getSimpleName(), "Getting Profile");
//
//		getProfile(new Listener<Profile>() {
//
//			@Override
//			public void onResponse(Profile profile) {
//
//				httpWorkerInterface.onProfileLoaded(profile);
//
//			}
//
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				httpWorkerInterface.onProfileError();
//
//			}
//		});
//	}

	public void getPrices(final HttpWorkerInterface interFace) {

		Log.d(getClass().getSimpleName(), "Getting Prices");
		
		if(interFace == null){
			return;
		}

		int source = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("price_source_preference", "0"));

		switch (source) {
		case PRICE_SOURCE_BITSTAMP_USD:
			getPricesBitStamp(new Listener<PricesBitStamp>() {

				@Override
				public void onResponse(PricesBitStamp prices) {

					GenericPrice price = new GenericPrice();
					
					try{
						price.setValueFloat(Float.parseFloat(prices.getLast()));
						price.setSymbol("$");
						
						interFace.onPricesLoaded(price);
					} catch (NullPointerException ignore){
						interFace.onPricesError();
					}

				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

					interFace.onPricesError();

				}
			});
			break;
		case PRICE_SOURCE_MTGOX_USD:

			getPricesMtGox("USD", new Listener<PricesMtGox>() {

				@Override
				public void onResponse(PricesMtGox prices) {

					
					try{
						// Write jsonData to PricesMtGox Object
						GenericPrice price = prices.getLastPrice();
						price.setSymbol("$");
						
						interFace.onPricesLoaded(price);
					} catch (NullPointerException ignore){
						interFace.onPricesError();
					}

				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {

					interFace.onPricesError();

				}
			});
			break;
		case PRICE_SOURCE_MTGOX_EUR:
			
			getPricesMtGox("EUR", new Listener<PricesMtGox>() {
				
				@Override
				public void onResponse(PricesMtGox prices) {
					
					try{
						// Write jsonData to PricesMtGox Object
						prices = App.parsePrices(prices.getData());
						
						GenericPrice price = prices.getLastPrice();
						price.setSymbol("€");
						
						interFace.onPricesLoaded(price);
					} catch (NullPointerException ignore){
						interFace.onPricesError();
					}
				}
				
			}, new ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					
					interFace.onPricesError();
					
				}
			});
			break;
		case PRICE_SOURCE_BTCE_USD:
			
			getPricesBTCe("USD", new Listener<PricesBTCe>() {
				
				@Override
				public void onResponse(PricesBTCe prices) {
					try{
						GenericPrice price = new GenericPrice();
						price.setValueFloat(prices.getTicker().getLast());
						price.setSymbol("$");
						
						interFace.onPricesLoaded(price);
					}catch (NullPointerException e){
						interFace.onPricesError();
					}
				}
				
			}, new ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					
					interFace.onPricesError();
					
				}
			});
			break;
		case PRICE_SOURCE_BTCE_EUR:
			
			getPricesBTCe("EUR", new Listener<PricesBTCe>() {
				
				@Override
				public void onResponse(PricesBTCe prices) {
					
					try{
						GenericPrice price = new GenericPrice();
						price.setValueFloat(prices.getTicker().getLast());
						price.setSymbol("€");
						
						interFace.onPricesLoaded(price);
					}catch (NullPointerException e){
						interFace.onPricesError();
					}
				}
				
			}, new ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					
					interFace.onPricesError();
					
				}
			});
			break;

		default:
			break;
		}

	}

//	private void getStats() {
//		Log.d(getClass().getSimpleName(), "Getting Stats");
//
//		App.getInstance().httpWorker.getStats(new Listener<Stats>() {
//
//			@Override
//			public void onResponse(Stats stats) {
//				httpWorkerInterface.onStatsLoaded(stats);
//
//			}
//
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError error) {
//				httpWorkerInterface.onStatsError();
//
//			}
//		});
//	}

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
