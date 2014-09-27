package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.model.coindesk.Bpi;

public class PricesCoinDesk implements Parcelable {

    // Attributes
    private Bpi bpi;

    // Constructor used for Parcelable
    public PricesCoinDesk(Parcel in) {
        bpi = in.readParcelable(Bpi.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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

    public Bpi getBpi() {
        return bpi;
    }


}
