package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonObject;

public class Prices implements Parcelable {

	// Attributes
	private String result;
	private JsonObject data;
	private Price lastPrice;
	
	
	// Standardconstructor
	public Prices(){}

	// Constructor used for Parcelable
	public Prices(Parcel in) {
		
		data = new JsonObject();
		data = App.getInstance().gson.fromJson(in.readString(), JsonObject.class);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(data.getAsString());
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

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public JsonObject getData() {
		return data;
	}

	public void setData(JsonObject data) {
		this.data = data;
	}

	public Price getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(Price lastPrice) {
		this.lastPrice = lastPrice;
	}

    
}
