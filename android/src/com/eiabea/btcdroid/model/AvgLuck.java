package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AvgLuck implements Parcelable {

	// Attributes
	private float avg_luck_7;
	private float avg_luck_30;
	private float avg_luck_1;
	
	// Standardconstructor
	public AvgLuck(){}

	// Constructor used for Parcelable
	public AvgLuck(Parcel in) {
		avg_luck_7 = in.readFloat();
		avg_luck_30 = in.readFloat();
		avg_luck_1 = in.readFloat();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(avg_luck_7);
		dest.writeFloat(avg_luck_30);
		dest.writeFloat(avg_luck_1);
		
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public AvgLuck createFromParcel(Parcel in) {
    		return new AvgLuck(in);
    	}
    	public AvgLuck[] newArray(int size) {
        	return new AvgLuck[size];
    	}
    };

	public float getAvg_luck_7() {
		return avg_luck_7;
	}

	public void setAvg_luck_7(float avg_luck_7) {
		this.avg_luck_7 = avg_luck_7;
	}

	public float getAvg_luck_30() {
		return avg_luck_30;
	}

	public void setAvg_luck_30(float avg_luck_30) {
		this.avg_luck_30 = avg_luck_30;
	}

	public float getAvg_luck_1() {
		return avg_luck_1;
	}

	public void setAvg_luck_1(float avg_luck_1) {
		this.avg_luck_1 = avg_luck_1;
	}

}
