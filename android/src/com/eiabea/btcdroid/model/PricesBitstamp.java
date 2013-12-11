package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PricesBitstamp implements Parcelable {

	// Attributes
	private String high;
	private String last;
	
	
	// Standardconstructor
	public PricesBitstamp(){}

	// Constructor used for Parcelable
	public PricesBitstamp(Parcel in) {
		in.writeString(high);
		in.writeString(last);

	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(high);
		dest.writeString(last);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public PricesBitstamp createFromParcel(Parcel in) {
    		return new PricesBitstamp(in);
    	}
    	public PricesBitstamp[] newArray(int size) {
        	return new PricesBitstamp[size];
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

}
