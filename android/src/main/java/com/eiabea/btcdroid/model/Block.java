package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

public class Block {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.ROUNDS_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String NUMBER = "number";
    public static final String MINING_DURATION = "mining_duration";
    public static final String REWARD = "reward";
    public static final String DATE_FOUND = "date_found";
    public static final String DATE_STARTED = "date_started";
    public static final String CONFIRMATIONS = "confirmations";

    // Database
    private long id;

    // Attributes
    private long number;
    private long mining_duration;
    private String reward;
    private String date_found;
    private String date_started;
    private int confirmations;


    // Standardconstructor
    public Block() {
    }

    public Block(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setNumber(c.getLong(c.getColumnIndex(NUMBER)));
        setMining_duration(c.getLong(c.getColumnIndex(MINING_DURATION)));
        setReward(c.getString(c.getColumnIndex(REWARD)));
        setDate_found(c.getString(c.getColumnIndex(DATE_FOUND)));
        setDate_started(c.getString(c.getColumnIndex(DATE_STARTED)));
        setConfirmations(c.getInt(c.getColumnIndex(CONFIRMATIONS)));

    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        values.put(NUMBER, getNumber());
        values.put(MINING_DURATION, getMining_duration());
        values.put(REWARD, getReward());
        values.put(DATE_FOUND, getDate_found());
        values.put(DATE_STARTED, getDate_started());
        values.put(CONFIRMATIONS, getConfirmations());

        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMining_duration() {
        return mining_duration;
    }

    public void setMining_duration(long mining_duration) {
        this.mining_duration = mining_duration;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
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

    public int getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }
}
