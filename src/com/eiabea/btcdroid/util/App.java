package com.eiabea.btcdroid.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Application;

import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class App extends Application {
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat dateStatsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()); 
	
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

	public static Prices parsePrices(JsonObject json) {
		Prices prices = new Prices();

		ArrayList<Price> listPrices = new ArrayList<Price>();

		prices.setTimestamp(json.get("timestamp").getAsLong());

		Set<Entry<String, JsonElement>> set = json.entrySet();

		for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonElement> current = it.next();

			if (!current.getKey().equals("timestamp")) {

				Price tmpPrice = new Price();

				tmpPrice.setCurrency(current.getKey());

				JsonElement jsonPrice = current.getValue();

				JsonElement value7d = jsonPrice.getAsJsonObject().get("7d");

				if (value7d != null) {
					tmpPrice.setT7d(value7d.getAsString());
				}

				JsonElement value30d = jsonPrice.getAsJsonObject().get("30d");

				if (value30d != null) {
					tmpPrice.setT30d(value30d.getAsString());
				}

				JsonElement value24h = jsonPrice.getAsJsonObject().get("24h");

				if (value24h != null) {
					tmpPrice.setT24h(value24h.getAsString());
				}

				listPrices.add(tmpPrice);

			}
		}

		prices.setPrices(listPrices);
		return prices;
	}
}
