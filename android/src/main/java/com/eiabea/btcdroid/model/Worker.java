package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

public class Worker {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.WORKER_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String NAME = "name";
    public static final String LAST_SHARE = "last_share";
    public static final String SCORE = "score";
    public static final String ALIVE = "alive";
    public static final String SHARES = "shares";
    public static final String HASHRATE = "hashrate";

    // Database
    private long id;

    // Attributes
    private String name;
    private long last_share;
    private String score;
    private boolean alive;
    private int shares;
    private int hashrate;

    // Standardconstructor
    public Worker() {
    }

    public Worker(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setName(c.getString(c.getColumnIndex(NAME)));
        setLast_share(c.getLong(c.getColumnIndex(LAST_SHARE)));
        setScore(c.getString(c.getColumnIndex(SCORE)));
        setAlive(c.getInt(c.getColumnIndex(ALIVE)) == 1);
        setShares(c.getInt(c.getColumnIndex(SHARES)));
        setHashrate(c.getInt(c.getColumnIndex(HASHRATE)));
    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        values.put(NAME, getName());
        values.put(LAST_SHARE, getLast_share());
        values.put(SCORE, getScore());
        values.put(ALIVE, isAlive());
        values.put(SHARES, getShares());
        values.put(HASHRATE, getHashrate());

        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public int getHashrate() {
        return hashrate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLast_share(long last_share) {
        this.last_share = last_share;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public void setHashrate(int hashrate) {
        this.hashrate = hashrate;
    }
}
