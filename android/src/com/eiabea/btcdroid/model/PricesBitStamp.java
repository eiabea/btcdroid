package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PricesBitStamp implements Parcelable {

	// Attributes
	private String high;
	private String last;
	private String timestamp;
	private String bid;
	private String volume;
	private String low;
	private String ask;

	
	// Standardconstructor
	public PricesBitStamp(){}

	// Constructor used for Parcelable
	public PricesBitStamp(Parcel in) {
		high = in.readString();
		last = in.readString();
		timestamp = in.readString();
		bid = in.readString();
		volume = in.readString();
		low = in.readString();
		ask = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(high);
		dest.writeString(last);
		dest.writeString(timestamp);
		dest.writeString(bid);
		dest.writeString(volume);
		dest.writeString(low);
		dest.writeString(ask);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public PricesBitStamp createFromParcel(Parcel in) {
    		return new PricesBitStamp(in);
    	}
    	public PricesBitStamp[] newArray(int size) {
        	return new PricesBitStamp[size];
    	}
    };


	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

}
