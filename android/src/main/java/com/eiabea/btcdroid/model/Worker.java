package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Worker implements Parcelable {

    // Attributes
    private String name;
    private long last_share;
    private String score;
    private boolean alive;
    private int shares;
    private float hashrate;

    // Constructor used for Parcelable
    public Worker(Parcel in) {
        name = in.readString();
        last_share = in.readLong();
        score = in.readString();
        alive = in.readByte() == 1;
        shares = in.readInt();
        hashrate = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeLong(last_share);
        dest.writeString(score);
        dest.writeByte((byte) (alive ? 1 : 0));
        dest.writeInt(shares);
        dest.writeFloat(hashrate);

    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Worker createFromParcel(Parcel in) {
            return new Worker(in);
        }

        public Worker[] newArray(int size) {
            return new Worker[size];
        }
    };

    public long getLast_share() {
        return last_share;
    }


    public String getScore() {
        return score;
    }


    public boolean isAlive() {
        return alive;
    }


    public int getShares() {
        return shares;
    }


    public float getHashrate() {
        // slush switched to GH/s --> * 1000
        return hashrate * 1000;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
