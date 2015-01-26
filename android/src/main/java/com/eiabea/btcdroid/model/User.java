package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

/**
 * Created by eiabea on 12/26/14.
 */
public class User {
    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.USER_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String USER_ID = "user_id";
    public static final String TOTAL_REWARDS = "total_rewards";
    public static final String PAID_REWARDS = "paid_rewards";
    public static final String UNPAID_REWARDS = "unpaid_rewards";
    public static final String PAST_24H_REWARDS = "past_24h_rewards";

    private long id;
    private long user_id;
    private double total_rewards;
    private double paid_rewards;
    private double unpaid_rewards;
    private double past_24h_rewards;

    public User(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setUser_id(c.getLong(c.getColumnIndex(USER_ID)));
        setTotal_rewards(c.getDouble(c.getColumnIndex(TOTAL_REWARDS)));
        setPaid_rewards(c.getDouble(c.getColumnIndex(PAID_REWARDS)));
        setUnpaid_rewards(c.getDouble(c.getColumnIndex(UNPAID_REWARDS)));
        setPast_24h_rewards(c.getDouble(c.getColumnIndex(PAST_24H_REWARDS)));
    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        values.put(USER_ID, getUser_id());
        values.put(TOTAL_REWARDS, getTotal_rewards());
        values.put(PAID_REWARDS, getPaid_rewards());
        values.put(UNPAID_REWARDS, getUnpaid_rewards());
        values.put(PAST_24H_REWARDS, getPast_24h_rewards());

        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public double getTotal_rewards() {
        return total_rewards;
    }

    public void setTotal_rewards(double total_rewards) {
        this.total_rewards = total_rewards;
    }

    public double getPaid_rewards() {
        return paid_rewards;
    }

    public void setPaid_rewards(double paid_rewards) {
        this.paid_rewards = paid_rewards;
    }

    public double getUnpaid_rewards() {
        return unpaid_rewards;
    }

    public void setUnpaid_rewards(double unpaid_rewards) {
        this.unpaid_rewards = unpaid_rewards;
    }

    public double getPast_24h_rewards() {
        return past_24h_rewards;
    }

    public void setPast_24h_rewards(double past_24h_rewards) {
        this.past_24h_rewards = past_24h_rewards;
    }
}
