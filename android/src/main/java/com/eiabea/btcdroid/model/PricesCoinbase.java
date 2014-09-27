package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.model.btce.BTCeTicker;
import com.eiabea.btcdroid.model.coinbase.Subtotal;

public class PricesCoinbase implements Parcelable {

    // Attributes
    private Subtotal subtotal;

    // Constructor used for Parcelable
    public PricesCoinbase(Parcel in) {
        subtotal = in.readParcelable(Subtotal.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(subtotal, flags);
    }

    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR = new Creator() {
        public PricesCoinbase createFromParcel(Parcel in) {
            return new PricesCoinbase(in);
        }

        public PricesCoinbase[] newArray(int size) {
            return new PricesCoinbase[size];
        }
    };

    public Subtotal getSubtotal() {
        return subtotal;
    }
}
