package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;
import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonObject;

public class Stats {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.STATS_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String JSON = "json";

    // Database
    private long id;
    private String json;

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

    public Stats(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setJson(c.getString(c.getColumnIndex(JSON)));
    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        values.put(JSON, getJson());

        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
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
