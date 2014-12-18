package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonObject;

public class Stats implements Parcelable {

    // Attributes
    private String round_duration;
    private String ghashes_ps;
    private String round_started;
    private String luck_1;
    private String luck_7;
    private String luck_30;
    private String shares_cdf;
    private long shares;
    private JsonObject blocks;

    // Standardconstructor
    public Stats() {
    }

    // Constructor used for Parcelable
    public Stats(Parcel in) {
        round_duration = in.readString();
        ghashes_ps = in.readString();
        round_started = in.readString();
        luck_1 = in.readString();
        luck_7 = in.readString();
        luck_30 = in.readString();
        shares_cdf = in.readString();

        shares = in.readLong();

        blocks = new JsonObject();
        blocks = App.getInstance().gson.fromJson(in.readString(), JsonObject.class);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(round_duration);
        dest.writeString(ghashes_ps);
        dest.writeString(round_started);
        dest.writeString(luck_1);
        dest.writeString(luck_7);
        dest.writeString(luck_30);
        dest.writeString(shares_cdf);

        dest.writeLong(shares);
        if(blocks != null){
            dest.writeString(blocks.toString());
        }
        
    }

    public String getShares_cdf() {
        return shares_cdf;
    }

    public void setShares_cdf(String shares_cdf) {
        this.shares_cdf = shares_cdf;
    }

    public JsonObject getBlocks() {
        return blocks;
    }

    public void setBlocks(JsonObject blocks) {
        this.blocks = blocks;
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Stats createFromParcel(Parcel in) {
            return new Stats(in);
        }

        public Stats[] newArray(int size) {
            return new Stats[size];
        }
    };

    public String getRound_duration() {
        return round_duration;
    }

    public void setRound_duration(String round_duration) {
        this.round_duration = round_duration;
    }

    public String getGhashes_ps() {
        return ghashes_ps;
    }

    public void setGhashes_ps(String ghashes_ps) {
        this.ghashes_ps = ghashes_ps;
    }

    public String getRound_started() {
        return round_started;
    }

    public void setRound_started(String round_started) {
        this.round_started = round_started;
    }

    public String getLuck_1() {
        return luck_1;
    }

    public void setLuck_1(String luck_1) {
        this.luck_1 = luck_1;
    }

    public String getLuck_7() {
        return luck_7;
    }

    public void setLuck_7(String luck_7) {
        this.luck_7 = luck_7;
    }

    public String getLuck_30() {
        return luck_30;
    }

    public void setLuck_30(String luck_30) {
        this.luck_30 = luck_30;
    }

    public long getShares() {
        return shares;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

}
