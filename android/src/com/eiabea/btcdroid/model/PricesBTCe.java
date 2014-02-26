package com.eiabea.btcdroid.model;

import com.eiabea.btcdroid.model.btce.BTCeTicker;

import android.os.Parcel;
import android.os.Parcelable;

public class PricesBTCe implements Parcelable {

	// Attributes
	private BTCeTicker ticker;

	
	// Standardconstructor
	public PricesBTCe(){}

	// Constructor used for Parcelable
	public PricesBTCe(Parcel in) {
		ticker = in.readParcelable(BTCeTicker.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(ticker, flags);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public PricesBTCe createFromParcel(Parcel in) {
    		return new PricesBTCe(in);
    	}
    	public PricesBTCe[] newArray(int size) {
        	return new PricesBTCe[size];
    	}
    };


	public BTCeTicker getTicker() {
		return ticker;
	}

	public void setTicker(BTCeTicker ticker) {
		this.ticker = ticker;
	}

}
