package com.eiabea.btcdroid.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Worker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class App extends Application {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat dateStatsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	public static final SimpleDateFormat dateDurationFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

	public static final String PREF_NAME = "app_data";
	public static final String PREF_TOKEN = "token";
	public static final String PREF_LAST_PRICE = "last_price";
	public static final String PREF_LAST_LUCK24 = "last_luck_24";
	public static final String PREF_LAST_LUCK7D = "last_luck_7d";
	public static final String PREF_LAST_LUCK30D = "last_luck_30d";

	private String token = "";
	private int threshold = 15;

	public HttpWorker httpWorker;

	public Gson gson;

	private SharedPreferences pref;

	private Price lastPrice;
	private float luck24, luck7d, luck30d;

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
		gson = new Gson();

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		this.lastPrice = gson.fromJson(pref.getString(PREF_LAST_PRICE, ""), Price.class);

		this.luck24 = pref.getFloat(PREF_LAST_LUCK24, 0.0f);
		this.luck7d = pref.getFloat(PREF_LAST_LUCK7D, 0.0f);
		this.luck30d = pref.getFloat(PREF_LAST_LUCK30D, 0.0f);

		token = pref.getString(PREF_TOKEN, "");
		threshold = Integer.valueOf(pref.getString("threshold", "15"));

		me = this;
		httpWorker = new HttpWorker(this.getApplicationContext(), token);

	}

	/**
	 * @return Instance of App.class
	 */
	public static App getInstance() {
		return me;
	}
	
	public void resetToken(){
		this.token = PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");
		
		setToken(this.token);
	}
	

	public void resetThreshold() {
		this.threshold = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getString("threshold", "15"));
	}
	
	public int getThreshold(){
		return this.threshold;
	}

	public void setToken(String token) {
		this.token = token;

		httpWorker = new HttpWorker(this.getApplicationContext(), token);
		
//		pref.edit().putString(PREF_TOKEN, token).commit();

	}

	public String getToken() {
		return this.token;
	}

	public boolean isTokenSet() {
		if (token != null && token.length() > 0) {
			return true;
		}
		return false;
	}

	public void setLastPrice(Price lastPrice) {
		this.lastPrice = lastPrice;
		pref.edit().putString(PREF_LAST_PRICE, gson.toJson(lastPrice)).commit();
	}

	public Price getLastPrice() {
		return this.lastPrice;
	}

	public float getLuck24() {
		return luck24;
	}

	public void setLuck24(float luck24) {
		this.luck24 = luck24;
		pref.edit().putFloat(PREF_LAST_LUCK24, luck24).commit();
	}

	public float getLuck7d() {
		return luck7d;
	}

	public void setLuck7d(float luck7d) {
		this.luck7d = luck7d;
		pref.edit().putFloat(PREF_LAST_LUCK7D, luck7d).commit();
	}

	public float getLuck30d() {
		return luck30d;
	}

	public void setLuck30d(float luck30d) {
		this.luck30d = luck30d;
		pref.edit().putFloat(PREF_LAST_LUCK30D, luck30d).commit();
	}

	public static Prices parsePrices(JsonObject json) {
		Prices prices = new Prices();

		Set<Entry<String, JsonElement>> set = json.entrySet();

		for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonElement> current = it.next();

			if (current.getKey().equals("last")) {

				JsonObject data = current.getValue().getAsJsonObject();

				Price tmpPrice = new Price();

				tmpPrice.setValue(data.get("value").getAsString());
				tmpPrice.setValue_int(data.get("value_int").getAsString());
				tmpPrice.setDisplay(data.get("display").getAsString());
				tmpPrice.setDisplay_short(data.get("display_short").getAsString());
				tmpPrice.setCurrency(data.get("currency").getAsString());

				prices.setLastPrice(tmpPrice);
			}

		}

		return prices;
	}

	public static List<Block> parseBlocks(JsonObject json) {
		List<Block> blocks = new ArrayList<Block>();

		Set<Entry<String, JsonElement>> set = json.entrySet();

		for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext();) {
			Entry<String, JsonElement> current = it.next();

			blocks.add(App.getInstance().gson.fromJson(current.getValue(), Block.class));

		}
		return blocks;
	}

	public static String formatHashRate(String hash) {
		double doubleHash = Double.valueOf(hash);
		if (doubleHash > 1000) {
			return String.format("%.2f", doubleHash / 1000d) + " GH/s";
		}
		return doubleHash + " MH/s";
	}

	public static String formatHashRate(int hash) {
		if (hash > 1000) {
			return String.format("%.2f", ((float) hash) / 1000f) + " GH/s";
		}
		return String.valueOf(hash) + " MH/s";
	}

	public static class sortWorkers implements Comparator<Worker> {

		@Override
		public int compare(Worker lhs, Worker rhs) {
			return (lhs.isAlive() ^ rhs.isAlive()) ? ((lhs.isAlive() ^ true) ? 1 : -1) : 0;
		}

	}
}
