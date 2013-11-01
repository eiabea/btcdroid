package com.eiabea.btcdroid.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class HttpWorker{

	public static final String BASEURL = "https://mining.bitcoin.cz/";
	
	public static final String DEBUG_API_KEY = "402189-0754bbdd5fa5ea39699830dd588986e5";
	
	public static final String STATS_URL = BASEURL + "/stats/json/" + DEBUG_API_KEY;
	public static final String PROFILE_URL = BASEURL + "/accounts/profile/json/";

	public static final int POST = 0;
	public static final int GET = 1;

	public static final int GET_PROFILE = 10;
	public static final int GET_STATS = 20;
	
	public static final int TIMEOUT_CONNECTION = 12000;
	public static final int TIMEOUT_SOCKET = 15000;

	public static final int FINE = 0;
	
	// Errorcodes
	public static final int ERR_NO_CONNECTION = -2;
	public static final int ERR_CONNECTION_TIMEOUT = -3;
	public static final int ERR_SOCKET_TIMEOUT = -4;
	public static final int ERR_EMPTY_RESPONSE = -5;
	public static final int ERR_JSON_PARSER = -6;

	private Context context;
	
	private String token = "";
	
	public static RequestQueue mQueue;
	
	public HttpWorker() {}

	public HttpWorker(Context context) {
		this.context = context;
		initVolley();
	}
	
	private void initVolley(){
		mQueue = Volley.newRequestQueue(context);
		mQueue.start();
	}
	
	public interface HttpWorkerInterface{
		public void requestDone(Activity context, Profile response);
		public void requestDone(Activity context, Stats response);
	}
	
	public void setToken(String token){
		this.token = token;
	}
	
	public boolean isTokenSet(){
		if(token != null && token.length() > 0){
			return true;
		}
		return false;
	}
	
	public void getProfile(final Activity context) {
		Log.d(getClass().getSimpleName(), "get Profile");
		
		String url = HttpWorker.PROFILE_URL + token;
		
		System.out.println(HttpWorker.mQueue.toString());
		
		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, new Response.Listener<Profile>() {
			@Override
			public void onResponse(Profile response) {
				
				Log.d(getClass().getSimpleName(), "get profile done");
				
				MainActivity act = new MainActivity();
				act.requestDone(context, response);
				
			}
		}, new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getSimpleName(), "Error while loading: " + error.getMessage());
			}
		}));
		
	}
	
	public void getStats(final Activity context) {
		Log.d(getClass().getSimpleName(), "get Stats");
		
		String url = HttpWorker.STATS_URL;
		
		System.out.println(HttpWorker.mQueue.toString());
		
		HttpWorker.mQueue.add(new GsonRequest<Stats>(url, Stats.class, null, new Response.Listener<Stats>() {
			@Override
			public void onResponse(Stats response) {
				
				Log.d(getClass().getSimpleName(), "get stats done");
				
				Log.d(getClass().getSimpleName(), "round_duration: " + response.getRound_duration());
				
				
				
			}
		}, new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getSimpleName(), "Error while loading: " + error.getMessage());
			}
		}));
		
	}
}
