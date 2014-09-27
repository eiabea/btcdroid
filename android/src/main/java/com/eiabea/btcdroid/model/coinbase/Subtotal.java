package com.eiabea.btcdroid.model.coinbase;

import android.os.Parcel;
import android.os.Parcelable;

public class Subtotal implements Parcelable {

    // Attributes
    private float amount;
    private String currency;

    // Constructor used for Parcelable
    public Subtotal(Parcel in) {
        amount = in.readFloat();
        currency = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(amount);
        dest.writeString(currency);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Subtotal createFromParcel(Parcel in) {
            return new Subtotal(in);
        }

        public Subtotal[] newArray(int size) {
            return new Subtotal[size];
        }
    };

    public float getAmount() {
        return amount;
    }

}
