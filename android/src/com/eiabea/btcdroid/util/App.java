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

	public HttpWorker httpWorker;

	public Gson gson;

	private SharedPreferences pref;

	private Price lastPrice;

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

		pref = getSharedPreferences("appData", MODE_PRIVATE);
		this.lastPrice = gson.fromJson(pref.getString("lastPrice", ""), Price.class);

		me = this;
		httpWorker = new HttpWorker(this.getApplicationContext());

	}

	/**
	 * @return Instance of App.class
	 */
	public static App getInstance() {
		return me;
	}

	public void setLastPrice(Price lastPrice) {
		this.lastPrice = lastPrice;
		pref.edit().putString("lastPrice", gson.toJson(lastPrice)).commit();
	}

	public Price getLastPrice() {
		return this.lastPrice;
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
		if(doubleHash > 1000){
			return String.format("%.2f", doubleHash / 1000d) + " GH/s";
		}
		return doubleHash + " MH/s";
	}

	public static String formatHashRate(int hash) {
		if(hash > 1000){
			return String.format("%.2f", ((float) hash) / 1000f) + " GH/s";
		}
		return String.valueOf(hash) + " MH/s";
	}

	public static class sortWorkers implements Comparator<Worker> {

		@Override
		public int compare(Worker lhs, Worker rhs) {
			return lhs.getName().compareTo(rhs.getName());
		}

	}
}
