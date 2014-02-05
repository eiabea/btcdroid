package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BTCeTicker implements Parcelable {
	// Attributes
	private float high;
	private float last;
	private float low;
	
	// Standardconstructor
	public BTCeTicker(){}

	// Constructor used for Parcelable
	public BTCeTicker(Parcel in) {
		high = in.readFloat();
		last = in.readFloat();
		low = in.readFloat();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(high);
		dest.writeFloat(last);
		dest.writeFloat(low);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public BTCeTicker createFromParcel(Parcel in) {
    		return new BTCeTicker(in);
    	}
    	public BTCeTicker[] newArray(int size) {
        	return new BTCeTicker[size];
    	}
    };

	public float getHigh() {
		return high;
	}

	public void setHigh(float high) {
		this.high = high;
	}

	public float getLast() {
		return last;
	}

	public void setLast(float last) {
		this.last = last;
	}

	public float getLow() {
		return low;
	}

	public void setLow(float low) {
		this.low = low;
	}
}
