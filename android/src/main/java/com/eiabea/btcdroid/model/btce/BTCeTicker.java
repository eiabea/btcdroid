package com.eiabea.btcdroid.model.btce;

import android.os.Parcel;
import android.os.Parcelable;

public class BTCeTicker implements Parcelable {
    // Attributes
    private float last;

    // Constructor used for Parcelable
    public BTCeTicker(Parcel in) {
        last = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(last);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public BTCeTicker createFromParcel(Parcel in) {
            return new BTCeTicker(in);
        }

        public BTCeTicker[] newArray(int size) {
            return new BTCeTicker[size];
        }
    };

    public float getLast() {
        return last;
    }
}
