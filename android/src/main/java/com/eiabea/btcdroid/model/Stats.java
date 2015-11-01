package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;
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
    private String round_started;
    private String luck_1;
    private String luck_7;
    private String luck_30;
    private String shares_cdf;
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

    public JsonObject getBlocks() {
        return blocks;
    }

    public String getRound_duration() {
        return round_duration;
    }

    public String getRound_started() {
        return round_started;
    }

    public String getLuck_1() {
        return luck_1;
    }

    public String getLuck_7() {
        return luck_7;
    }

    public String getLuck_30() {
        return luck_30;
    }


}
