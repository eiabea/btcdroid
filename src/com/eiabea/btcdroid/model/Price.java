package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Price implements Parcelable {

	// Attributes
	private String value;
	private String value_int;
	private String display;
	private String display_short;
	private String currency;
	
	
	// Standardconstructor
	public Price(){}

	// Constructor used for Parcelable
	public Price(Parcel in) {
		value = in.readString();
		value_int = in.readString();
		display = in.readString();
		display_short = in.readString();
		currency = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(value);
		dest.writeString(value_int);
		dest.writeString(display);
		dest.writeString(display_short);
		dest.writeString(currency);
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue_int() {
		return value_int;
	}

	public void setValue_int(String value_int) {
		this.value_int = value_int;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public String getDisplay_short() {
		return display_short;
	}

	public void setDisplay_short(String display_short) {
		this.display_short = display_short;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	

//	public ArrayList<Worker> getWorkersList(){
//		
//		listWorkers = new ArrayList<Worker>();
//		
//		Set<Entry<String, JsonElement>> set =  workers.entrySet();
//		
//	    for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext(); ) {
//	    	Entry<String, JsonElement> current = it.next();
//	   
//	    	Worker tmpWorker = App.getInstance().gson.fromJson(current.getValue(), Worker.class);
//	    	tmpWorker.setName(current.getKey());
//	    	
//	    	listWorkers.add(tmpWorker);
//	    }
//	    
//	    return listWorkers;
//	}
    
}
