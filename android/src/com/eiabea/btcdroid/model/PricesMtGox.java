package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonObject;

public class PricesMtGox implements Parcelable {

	// Attributes
	private String result;
	private JsonObject data;
	private Price lastPrice;
	
	
	// Standardconstructor
	public PricesMtGox(){}

	// Constructor used for Parcelable
	public PricesMtGox(Parcel in) {
		
		data = new JsonObject();
		data = App.getInstance().gson.fromJson(in.readString(), JsonObject.class);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(data.toString());
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public PricesMtGox createFromParcel(Parcel in) {
    		return new PricesMtGox(in);
    	}
    	public PricesMtGox[] newArray(int size) {
        	return new PricesMtGox[size];
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
