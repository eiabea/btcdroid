package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Block implements Parcelable {
    // Attributes
    private String mining_duration;
    private String reward;
    private String date_found;
    private String date_started;
    private int confirmations;


    // Standardconstructor
    public Block() {
    }

    // Constructor used for Parcelable
    public Block(Parcel in) {
        mining_duration = in.readString();
        reward = in.readString();
        date_found = in.readString();
        date_started = in.readString();
        confirmations = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mining_duration);
        dest.writeString(reward);
        dest.writeString(date_found);
        dest.writeString(date_started);
        dest.writeInt(confirmations);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Block createFromParcel(Parcel in) {
            return new Block(in);
        }

        public Block[] newArray(int size) {
            return new Block[size];
        }
    };


    public String getMining_duration() {
        return mining_duration;
    }

    public void setMining_duration(String mining_duration) {
        this.mining_duration = mining_duration;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public String getDate_found() {
        return date_found;
    }

    public void setDate_found(String date_found) {
        this.date_found = date_found;
    }

    public String getDate_started() {
        return date_started;
    }

    public void setDate_started(String date_started) {
        this.date_started = date_started;
    }

}