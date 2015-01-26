package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;

/**
 * Created by eiabea on 12/26/14.
 */
public class Pool {
    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.POOL_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String POOL_SPEED = "pool_speed";
    public static final String DIFFICULTY = "difficulty";

    // Database
    private long id;

    // Attributes
    double pool_speed;
    long difficulty;

    public Pool(Cursor c) {
        setId(c.getLong(c.getColumnIndex(_ID)));
        setPool_speed(c.getDouble(c.getColumnIndex(POOL_SPEED)));
        setDifficulty(c.getLong(c.getColumnIndex(DIFFICULTY)));
    }

    public ContentValues getContentValues(boolean forInsert) {
        ContentValues values = new ContentValues();

        if (forInsert) {
            //values.put(_ID, getId());
        }

        values.put(POOL_SPEED, getPool_speed());
        values.put(DIFFICULTY, getDifficulty());

        return values;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPool_speed() {
        return pool_speed;
    }

    public void setPool_speed(double pool_speed) {
        this.pool_speed = pool_speed;
    }

    public long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }
}
