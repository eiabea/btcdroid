package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Block implements Parcelable{
	// Attributes
	private String mining_duration;
	
	
	// Standardconstructor
	public Block(){}

	// Constructor used for Parcelable
	public Block(Parcel in) {
		mining_duration = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mining_duration);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public Price createFromParcel(Parcel in) {
    		return new Price(in);
    	}
    	public Price[] newArray(int size) {
        	return new Price[size];
    	}
    };


	public String getMining_duration() {
		return mining_duration;
	}

	public void setMining_duration(String mining_duration) {
		this.mining_duration = mining_duration;
	}
    
}
