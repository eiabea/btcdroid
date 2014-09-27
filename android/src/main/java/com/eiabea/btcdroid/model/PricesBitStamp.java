package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PricesBitStamp implements Parcelable {

    // Attributes
    private String last;

    // Constructor used for Parcelable
    public PricesBitStamp(Parcel in) {
        last = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(last);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public PricesBitStamp createFromParcel(Parcel in) {
            return new PricesBitStamp(in);
        }

        public PricesBitStamp[] newArray(int size) {
            return new PricesBitStamp[size];
        }
    };


    public String getLast() {
        return last;
    }


}
