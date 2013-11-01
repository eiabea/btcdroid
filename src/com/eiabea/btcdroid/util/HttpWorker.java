package com.eiabea.btcdroid.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpWorker {

	public static final String BASEURL = "https://mining.bitcoin.cz/";
	
	public static final String DEBUG_API_KEY = "402189-0754bbdd5fa5ea39699830dd588986e5";
	
	public static final String GENERAL_URL = BASEURL + "/stats/json/" + DEBUG_API_KEY;
	public static final String PROFILE_URL = BASEURL + "/accounts/profile/json/" + DEBUG_API_KEY;

	public static final int POST = 0;
	public static final int GET = 1;
	
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
	
	public static RequestQueue mQueue;

	public HttpWorker(Context context) {
		this.context = context;
		initVolley();
	}
	
	private void initVolley(){
		mQueue = Volley.newRequestQueue(context);
		mQueue.start();
	}
}
