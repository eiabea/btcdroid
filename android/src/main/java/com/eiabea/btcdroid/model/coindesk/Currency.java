package com.eiabea.btcdroid.model.coindesk;

import android.os.Parcel;
import android.os.Parcelable;

public class Currency implements Parcelable {

    // Attributes
    private float rate_float;

    // Constructor used for Parcelable
    public Currency(Parcel in) {
        rate_float = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    public float getRate_float() {
        return rate_float;
    }

}
