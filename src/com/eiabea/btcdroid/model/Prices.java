package com.eiabea.btcdroid.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonObject;

public class Prices implements Parcelable {

	// Attributes
	private long timestamp;
	private JsonObject allPrices;
	private ArrayList<Price> prices;
	
	
	// Standardconstructor
	public Prices(){}

	// Constructor used for Parcelable
	public Prices(Parcel in) {
		timestamp = in.readLong();
		
		allPrices = new JsonObject();
		allPrices = App.getInstance().gson.fromJson(in.readString(), JsonObject.class);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(timestamp);

		dest.writeString(allPrices.getAsString());
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public Prices createFromParcel(Parcel in) {
    		return new Prices(in);
    	}
    	public Prices[] newArray(int size) {
        	return new Prices[size];
    	}
    };


	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public JsonObject getAllPrices() {
		return allPrices;
	}

	public void setAllPrices(JsonObject allPrices) {
		this.allPrices = allPrices;
	}

	public ArrayList<Price> getPrices() {
		return prices;
	}

	public void setPrices(ArrayList<Price> prices) {
		this.prices = prices;
	}


	
//	public ArrayList<Worker> getWorkersList(){
//		
//		listWorkers = new ArrayList<Worker>();
//		
//		Set<Entry<String, JsonElement>> set =  workers.entrySet();
//		
//	    for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext(); ) {
//	    	Entry<String, JsonElement> current = it.next();
//	   
//	    	Worker tmpWorker = App.getInstance().gson.fromJson(current.getValue(), Worker.class);
//	    	tmpWorker.setName(current.getKey());
//	    	
//	    	listWorkers.add(tmpWorker);
//	    }
//	    
//	    return listWorkers;
//	}
    
}
