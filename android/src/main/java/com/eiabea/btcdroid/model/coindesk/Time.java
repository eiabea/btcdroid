package com.eiabea.btcdroid.model.coindesk;

import android.os.Parcel;
import android.os.Parcelable;

public class Time implements Parcelable {

    // Attributes
    private String updated;
    private String updatedISO;
    private String updateduk;


    // Standardconstructor
    public Time() {
    }

    // Constructor used for Parcelable
    public Time(Parcel in) {
        updated = in.readString();
        updatedISO = in.readString();
        updateduk = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(updated);
        dest.writeString(updatedISO);
        dest.writeString(updateduk);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Time createFromParcel(Parcel in) {
            return new Time(in);
        }

        public Time[] newArray(int size) {
            return new Time[size];
        }
    };


    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUpdatedISO() {
        return updatedISO;
    }

    public void setUpdatedISO(String updatedISO) {
        this.updatedISO = updatedISO;
    }

    public String getUpdateduk() {
        return updateduk;
    }

    public void setUpdateduk(String updateduk) {
        this.updateduk = updateduk;
    }

}
