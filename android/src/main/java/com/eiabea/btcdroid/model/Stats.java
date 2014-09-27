package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Stats implements Parcelable {

    // Attributes
    private String round_duration;
    private String round_started;
    private String luck_1;
    private String luck_30;
    private String shares_cdf;
    private ArrayList<Block> blocks;
    private long shares;
    private float luck_7;

    // Constructor used for Parcelable
    public Stats(Parcel in) {
        round_duration = in.readString();
        round_started = in.readString();
        luck_1 = in.readString();
        luck_30 = in.readString();
        shares_cdf = in.readString();

        blocks = new ArrayList<Block>();
        in.readList(blocks, Block.class.getClassLoader());

        shares = in.readLong();
        luck_7 = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(round_duration);
        dest.writeString(round_started);
        dest.writeString(luck_1);
        dest.writeString(luck_30);
        dest.writeString(shares_cdf);

        dest.writeList(blocks);

        dest.writeLong(shares);
        dest.writeFloat(luck_7);

    }

    public String getShares_cdf() {
        return shares_cdf;
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

    public String getRound_started() {
        return round_started;
    }

    public String getLuck_1() {
        return luck_1;
    }

    public float getLuck_7() {
        return luck_7;
    }

    public String getLuck_30() {
        return luck_30;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }
}
