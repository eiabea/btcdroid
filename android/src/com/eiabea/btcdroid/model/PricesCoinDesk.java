package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.model.coindesk.Bpi;
import com.eiabea.btcdroid.model.coindesk.Time;

public class PricesCoinDesk implements Parcelable {

	// Attributes
	private Time time;
	private String disclaimer;
	private Bpi bpi;

	
	// Standardconstructor
	public PricesCoinDesk(){}

	// Constructor used for Parcelable
	public PricesCoinDesk(Parcel in) {
		time = in.readParcelable(Time.class.getClassLoader());
		disclaimer = in.readString();
		bpi = in.readParcelable(Bpi.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(time, flags);
		dest.writeString(disclaimer);
		dest.writeParcelable(bpi, flags);
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    	public PricesCoinDesk createFromParcel(Parcel in) {
    		return new PricesCoinDesk(in);
    	}
    	public PricesCoinDesk[] newArray(int size) {
        	return new PricesCoinDesk[size];
    	}
    };


	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public String getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(String disclaimer) {
		this.disclaimer = disclaimer;
	}

	public Bpi getBpi() {
		return bpi;
	}

	public void setBpi(Bpi bpi) {
		this.bpi = bpi;
	}

}
