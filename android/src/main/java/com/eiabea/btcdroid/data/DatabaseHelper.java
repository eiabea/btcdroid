package com.eiabea.btcdroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eiabea.btcdroid.model.AvgLuck;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public static final String PROFILE_TABLE_NAME = "profile";
    public static final String STATS_TABLE_NAME = "stats";
    public static final String PRICE_TABLE_NAME = "price";
    public static final String AVG_LUCK_TABLE_NAME = "avg_luck";

    private static final int DATABASE_VERSION = 9;
    private static final String DATABASE_NAME = "btcdroid";

    private static final String CREATE_PROFILE_TABLE =
            " CREATE TABLE " + PROFILE_TABLE_NAME +
                    " (" + Profile._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Profile.JSON + " TEXT " +
                    ");";

    private static final String CREATE_STATS_TABLE =
            " CREATE TABLE " + STATS_TABLE_NAME +
                    " (" + Stats._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Stats.JSON + " TEXT " +
                    ");";

    private static final String CREATE_PRICE_TABLE =
            " CREATE TABLE " + PRICE_TABLE_NAME +
                    " (" + GenericPrice._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + GenericPrice.JSON + " TEXT " +
                    ");";

    private static final String CREATE_AVG_LUCK_TABLE =
            " CREATE TABLE " + AVG_LUCK_TABLE_NAME +
                    " (" + AvgLuck._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + AvgLuck.JSON + " TEXT " +
                    ");";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROFILE_TABLE);
        db.execSQL(CREATE_STATS_TABLE);
        db.execSQL(CREATE_PRICE_TABLE);
        db.execSQL(CREATE_AVG_LUCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "Upgrading Database from v" + oldVersion + " to v" + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STATS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AVG_LUCK_TABLE_NAME);
        onCreate(db);
    }
}
