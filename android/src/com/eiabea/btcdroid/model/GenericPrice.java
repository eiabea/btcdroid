package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GenericPrice implements Parcelable {

	// Attributes
	private float valueFloat;

	// Standardconstructor
	public GenericPrice(){}

	// Constructor used for Parcelable
	public GenericPrice(Parcel in) {
		valueFloat = in.readFloat();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeFloat(valueFloat);

	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public GenericPrice createFromParcel(Parcel in) {
    		return new GenericPrice(in);
    	}
    	public GenericPrice[] newArray(int size) {
        	return new GenericPrice[size];
    	}
    };

	public float getValueFloat() {
		return valueFloat;
	}

	public void setValueFloat(float valueFloat) {
		this.valueFloat = valueFloat;
	}

    
}
