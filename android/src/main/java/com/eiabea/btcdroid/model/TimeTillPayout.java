package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

import java.util.Date;

public class TimeTillPayout {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.TIME_TILL_PAYOUT_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String AVERAGE_TIME = "average_time";
    public static final String REMAINING_REWARD = "remaining_reward";
    public static final String AVG_BTC_PER_BLOCK = "avg_btc_per_block";

    // Database
    private long id;

    // Attributes
    private long averageTime;
    private double remainingReward;
    private double avgBtcPerBlock;

    public TimeTillPayout(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setAverageTime(c.getLong(c.getColumnIndex(AVERAGE_TIME)));
        setRemainingReward(c.getDouble(c.getColumnIndex(REMAINING_REWARD)));
        setAvgBtcPerBlock(c.getDouble(c.getColumnIndex(AVG_BTC_PER_BLOCK)));
    }

    /**
     * @return returns the time until the next payout in nanoseconds
     */
    public long getTimeTillPayout(){

        if(getAverageTime() == 0 || getRemainingReward() == 0 || getAvgBtcPerBlock() == 0){
            return 0;
        }

        // ((threshold - unconfirmed) / avg_block) * avg_time

        return (long) ((getRemainingReward() / getAvgBtcPerBlock()) * getAverageTime());
    }

    public TimeTillPayout(Profile profile) {

        if (profile == null) {
            return;
        }

        setId(1);
        setRemainingReward(Float.valueOf(profile.getSend_threshold()) - Float.valueOf(profile.getConfirmed_reward()));
    }

    public TimeTillPayout(Stats stats, Context context) {

        if (stats == null) {
            return;
        }

        setId(1);
        setAverageTime(Stats.getAverageRoundTime(context).getTime());
        setAvgBtcPerBlock(Block.getAverageReward(context));
    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        if (getAverageTime() != 0) {
            values.put(AVERAGE_TIME, getAverageTime());
        }
        if (getRemainingReward() != 0) {
            values.put(REMAINING_REWARD, getRemainingReward());
        }
        if (getAvgBtcPerBlock() != 0) {
            values.put(AVG_BTC_PER_BLOCK, getAvgBtcPerBlock());
        }

        return values;
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    public double getRemainingReward() {
        return remainingReward;
    }

    public void setRemainingReward(double remainingReward) {
        this.remainingReward = remainingReward;
    }

    public double getAvgBtcPerBlock() {
        return avgBtcPerBlock;
    }

    public void setAvgBtcPerBlock(double avgBtcPerBlock) {
        this.avgBtcPerBlock = avgBtcPerBlock;
    }
}
