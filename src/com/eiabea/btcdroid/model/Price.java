package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Price implements Parcelable {

	// Attributes
	private String currency;
	private String t7d;
	private String t30d;
	private String t24h;
	
	
	// Standardconstructor
	public Price(){}

	// Constructor used for Parcelable
	public Price(Parcel in) {
		currency = in.readString();
		t7d = in.readString();
		t30d = in.readString();
		t24h = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(currency);
		dest.writeString(t7d);
		dest.writeString(t30d);
		dest.writeString(t24h);
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

	public String getT7d() {
		return t7d;
	}

	public void setT7d(String t7d) {
		this.t7d = t7d;
	}

	public String getT30d() {
		return t30d;
	}

	public void setT30d(String t30d) {
		this.t30d = t30d;
	}

	public String getT24h() {
		return t24h;
	}

	public void setT24h(String t24h) {
		this.t24h = t24h;
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
