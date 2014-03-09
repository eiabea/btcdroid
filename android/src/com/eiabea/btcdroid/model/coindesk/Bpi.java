package com.eiabea.btcdroid.model.coindesk;

import android.os.Parcel;
import android.os.Parcelable;

public class Bpi implements Parcelable {

	// Attributes
	private Currency USD;
	private Currency EUR;
	private Currency GBP;

	
	// Standardconstructor
	public Bpi(){}

	// Constructor used for Parcelable
	public Bpi(Parcel in) {
		USD = in.readParcelable(Currency.class.getClassLoader());
		EUR = in.readParcelable(Currency.class.getClassLoader());
		GBP = in.readParcelable(Currency.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(USD, flags);
		dest.writeParcelable(EUR, flags);
		dest.writeParcelable(GBP, flags);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public Bpi createFromParcel(Parcel in) {
    		return new Bpi(in);
    	}
    	public Bpi[] newArray(int size) {
        	return new Bpi[size];
    	}
    };


	public Currency getUSD() {
		return USD;
	}

	public void setUSD(Currency uSD) {
		USD = uSD;
	}

	public Currency getEUR() {
		return EUR;
	}

	public void setEUR(Currency eUR) {
		EUR = eUR;
	}

	public Currency getGBP() {
		return GBP;
	}

	public void setGBP(Currency gBP) {
		GBP = gBP;
	}
    
}
