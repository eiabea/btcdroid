package com.eiabea.btcdroid.model.coindesk;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable {

	// Attributes
	private String code;
	private String rate;
	private String description;
	private float rate_float;

	
	// Standardconstructor
	public Currency(){}

	// Constructor used for Parcelable
	public Currency(Parcel in) {
		code = in.readString();
		rate = in.readString();
		description = in.readString();
		rate_float = in.readFloat();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(code);
		dest.writeString(rate);
		dest.writeString(description);
		dest.writeFloat(rate_float);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public Currency createFromParcel(Parcel in) {
    		return new Currency(in);
    	}
    	public Currency[] newArray(int size) {
        	return new Currency[size];
    	}
    };


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getRate_float() {
		return rate_float;
	}

	public void setRate_float(float rate_float) {
		this.rate_float = rate_float;
	}

}
