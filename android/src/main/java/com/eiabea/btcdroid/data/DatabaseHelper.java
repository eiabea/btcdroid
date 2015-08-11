package com.eiabea.btcdroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eiabea.btcdroid.model.AvgLuck;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public static final String PROFILE_TABLE_NAME = "profile";
    public static final String STATS_TABLE_NAME = "stats";
    public static final String PRICE_TABLE_NAME = "price";
    public static final String AVG_LUCK_TABLE_NAME = "avg_luck";
    public static final String WORKER_TABLE_NAME = "worker";
    public static final String ROUNDS_TABLE_NAME = "rounds";

    private static final int DATABASE_VERSION = 14;
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

    private static final String CREATE_WORKER_TABLE =
            " CREATE TABLE " + WORKER_TABLE_NAME +
                    " (" + Worker._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Worker.NAME + " TEXT, " +
                    " " + Worker.LAST_SHARE + " INTEGER, " +
                    " " + Worker.SCORE + " TEXT, " +
                    " " + Worker.ALIVE + " INTEGER, " +
                    " " + Worker.SHARES + " INTEGER, " +
                    " " + Worker.HASHRATE + " INTEGER " +
                    ");";

    private static final String CREATE_ROUNDS_TABLE =
            " CREATE TABLE " + ROUNDS_TABLE_NAME +
                    " (" + Block._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Block.NUMBER + " INTEGER, " +
                    " " + Block.MINING_DURATION + " INTEGER, " +
                    " " + Block.REWARD + " TEXT, " +
                    " " + Block.DATE_FOUND + " TEXT, " +
                    " " + Block.DATE_STARTED + " TEXT, " +
                    " " + Block.CONFIRMATIONS + " INTEGER " +
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
        db.execSQL(CREATE_WORKER_TABLE);
        db.execSQL(CREATE_ROUNDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "Upgrading Database from v" + oldVersion + " to v" + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STATS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRICE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AVG_LUCK_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ROUNDS_TABLE_NAME);
        onCreate(db);
    }
}
