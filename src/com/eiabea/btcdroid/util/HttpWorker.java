package com.eiabea.btcdroid.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class HttpWorker {

	public static final String BASEURL = "https://mining.bitcoin.cz/stats/json/";

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

	public static final String[] allCommands = {
		"categories/",
		"shops/",
		"events/",
		"offers/",
		"banners/",
		"contentForSiteID/",
		"mapsForShopID/",
		"imageForURL/",
		"menuElements/"
		};
	
	public static final int GET_CATEGORIES = 0;
	
	public static final int[] allContentForSite = {
		421,
		422,
		423,
		424,
		425,
		426
	};
	
	public HttpWorker(Context context) {
		this.context = context;
		initVolley();
	}
	
	private void initVolley(){
		mQueue = Volley.newRequestQueue(context);
		mQueue.start();
	}
}
