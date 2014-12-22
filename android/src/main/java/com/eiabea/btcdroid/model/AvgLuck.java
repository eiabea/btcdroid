package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

public class AvgLuck {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.AVG_LUCK_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String JSON = "json";

    // Database
    private long id;
    private String json;

    // Attributes
    private float avg_luck;

    // Standardconstructor
    public AvgLuck() {
    }

    public AvgLuck(Cursor c) {
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

    public float getAvg_luck() {
        return avg_luck;
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
}
