package com.eiabea.btcdroid.util;

import android.app.Application;

import com.google.gson.Gson;

public class App extends Application {
	public HttpWorker httpWorker;

	public Gson gson;

	/**
	 * Object of own Class
	 */
	private static App me;

	/**
	 * Initialize itself and all needed Helper
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		me = this;
		gson = new Gson();
		httpWorker = new HttpWorker(this.getApplicationContext());

	}
	
	/**
	 * @return Instance of App.class
	 */
	public static App getInstance() {
		return me;
	}
}
