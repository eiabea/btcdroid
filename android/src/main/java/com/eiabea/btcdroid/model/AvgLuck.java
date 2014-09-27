package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AvgLuck implements Parcelable {

    // Attributes
    private float avg_luck;

    // Constructor used for Parcelable
    public AvgLuck(Parcel in) {
        avg_luck = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(avg_luck);

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

    public float getAvg_luck() {
        return avg_luck;
    }


}
