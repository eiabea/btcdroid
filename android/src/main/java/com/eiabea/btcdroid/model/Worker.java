package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

public class Worker {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.WORKERS_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String WORKER_NAME = "worker_name";
    public static final String HASHRATE = "hashrate";
    public static final String VALID_SHARES = "valid_shares";
    public static final String STALE_SHARES = "stale_shares";
    public static final String DUPE_SHARES = "dupe_shares";
    public static final String UNKNOWN_SHARES = "unknown_shares";
    public static final String VALID_SHARES_SINCE_RESET = "valid_shares_since_reset";
    public static final String STALE_SHARES_SINCE_RESET = "stale_shares_since_reset";
    public static final String DUPE_SHARES_SINCE_RESET = "dupe_shares_since_reset";
    public static final String UNKNOWN_SHARES_SINCE_RESET = "unknown_shares_since_reset";
    public static final String ALIVE = "alive";
    public static final String LAST_SHARE = "last_share";

    // Database
    private long id;

    // Attributes
    boolean alive;
    String worker_name;
    long hash_rate;
    long valid_shares;
    long stale_shares;
    long dupe_shares;
    long unknown_shares;
    long valid_shares_since_reset;
    long stale_shares_since_reset;
    long dupe_shares_since_reset;
    long unknown_shares_since_reset;
    long last_share;

    // Standardconstructor
    public Worker() {
    }

    public Worker(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setWorker_name(c.getString(c.getColumnIndex(WORKER_NAME)));
        setHash_rate(c.getLong(c.getColumnIndex(HASHRATE)));
        setValid_shares(c.getLong(c.getColumnIndex(VALID_SHARES)));
        setStale_shares(c.getLong(c.getColumnIndex(STALE_SHARES)));
        setDupe_shares(c.getLong(c.getColumnIndex(DUPE_SHARES)));
        setUnknown_shares(c.getLong(c.getColumnIndex(UNKNOWN_SHARES)));
        setValid_shares_since_reset(c.getLong(c.getColumnIndex(VALID_SHARES_SINCE_RESET)));
        setStale_shares_since_reset(c.getLong(c.getColumnIndex(STALE_SHARES_SINCE_RESET)));
        setDupe_shares_since_reset(c.getLong(c.getColumnIndex(DUPE_SHARES_SINCE_RESET)));
        setUnknown_shares_since_reset(c.getLong(c.getColumnIndex(UNKNOWN_SHARES_SINCE_RESET)));
        setAlive(c.getInt(c.getColumnIndex(ALIVE)) == 1);
        setLast_share(c.getLong(c.getColumnIndex(LAST_SHARE)));
    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        values.put(WORKER_NAME, getWorker_name());
        values.put(HASHRATE, getHash_rate());
        values.put(VALID_SHARES, getValid_shares());
        values.put(STALE_SHARES, getStale_shares());
        values.put(DUPE_SHARES, getDupe_shares());
        values.put(UNKNOWN_SHARES, getUnknown_shares());
        values.put(VALID_SHARES_SINCE_RESET, getValid_shares_since_reset());
        values.put(STALE_SHARES_SINCE_RESET, getStale_shares_since_reset());
        values.put(DUPE_SHARES_SINCE_RESET, getDupe_shares_since_reset());
        values.put(UNKNOWN_SHARES_SINCE_RESET, getUnknown_shares_since_reset());
        values.put(ALIVE, isAlive());
        values.put(LAST_SHARE, getLast_share());

        return values;
    }

    public boolean isAlive(){
        return getHash_rate() > 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWorker_name() {
        return worker_name;
    }

    public void setWorker_name(String worker_name) {
        this.worker_name = worker_name;
    }

    public long getHash_rate() {
        return hash_rate;
    }

    public void setHash_rate(long hash_rate) {
        this.hash_rate = hash_rate;
    }

    public long getValid_shares() {
        return valid_shares;
    }

    public void setValid_shares(long valid_shares) {
        this.valid_shares = valid_shares;
    }

    public long getStale_shares() {
        return stale_shares;
    }

    public void setStale_shares(long stale_shares) {
        this.stale_shares = stale_shares;
    }

    public long getDupe_shares() {
        return dupe_shares;
    }

    public void setDupe_shares(long dupe_shares) {
        this.dupe_shares = dupe_shares;
    }

    public long getUnknown_shares() {
        return unknown_shares;
    }

    public void setUnknown_shares(long unknown_shares) {
        this.unknown_shares = unknown_shares;
    }

    public long getValid_shares_since_reset() {
        return valid_shares_since_reset;
    }

    public void setValid_shares_since_reset(long valid_shares_since_reset) {
        this.valid_shares_since_reset = valid_shares_since_reset;
    }

    public long getStale_shares_since_reset() {
        return stale_shares_since_reset;
    }

    public void setStale_shares_since_reset(long stale_shares_since_reset) {
        this.stale_shares_since_reset = stale_shares_since_reset;
    }

    public long getDupe_shares_since_reset() {
        return dupe_shares_since_reset;
    }

    public void setDupe_shares_since_reset(long dupe_shares_since_reset) {
        this.dupe_shares_since_reset = dupe_shares_since_reset;
    }

    public long getUnknown_shares_since_reset() {
        return unknown_shares_since_reset;
    }

    public void setUnknown_shares_since_reset(long unknown_shares_since_reset) {
        this.unknown_shares_since_reset = unknown_shares_since_reset;
    }

    public long getLast_share() {
        return last_share;
    }

    public void setLast_share(long last_share) {
        this.last_share = last_share;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
