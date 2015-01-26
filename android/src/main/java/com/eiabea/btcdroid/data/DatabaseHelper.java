package com.eiabea.btcdroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Pool;
import com.eiabea.btcdroid.model.User;
import com.eiabea.btcdroid.model.Worker;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public static final String POOL_TABLE_NAME = "pool";
    public static final String WORKERS_TABLE_NAME = "workers";
    public static final String USER_TABLE_NAME = "user";
    public static final String PRICE_TABLE_NAME = "price";

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "btcdroid_btcguild";

    private static final String CREATE_PRICE_TABLE =
            " CREATE TABLE " + PRICE_TABLE_NAME +
                    " (" + GenericPrice._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + GenericPrice.JSON + " TEXT " +
                    ");";

    private static final String CREATE_POOL_TABLE =
            " CREATE TABLE " + POOL_TABLE_NAME +
                    " (" + Pool._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Pool.POOL_SPEED + " INTEGER, " +
                    " " + Pool.DIFFICULTY + " INTEGER " +
                    ");";

    private static final String CREATE_WORKERS_TABLE =
            " CREATE TABLE " + WORKERS_TABLE_NAME +
                    " (" + Worker._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Worker.WORKER_NAME + " TEXT, " +
                    " " + Worker.HASHRATE + " INTEGER, " +
                    " " + Worker.VALID_SHARES + " INTEGER, " +
                    " " + Worker.STALE_SHARES + " INTEGER, " +
                    " " + Worker.DUPE_SHARES + " INTEGER, " +
                    " " + Worker.UNKNOWN_SHARES + " INTEGER, " +
                    " " + Worker.VALID_SHARES_SINCE_RESET + " INTEGER, " +
                    " " + Worker.STALE_SHARES_SINCE_RESET + " INTEGER, " +
                    " " + Worker.DUPE_SHARES_SINCE_RESET + " INTEGER, " +
                    " " + Worker.UNKNOWN_SHARES_SINCE_RESET + " INTEGER, " +
                    " " + Worker.LAST_SHARE + " INTEGER " +
                    ");";

    private static final String CREATE_USER_TABLE =
            " CREATE TABLE " + USER_TABLE_NAME +
                    " (" + User._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + User.USER_ID + " INTEGER, " +
                    " " + User.TOTAL_REWARDS + " INTEGER, " +
                    " " + User.PAID_REWARDS + " INTEGER, " +
                    " " + User.UNPAID_REWARDS + " INTEGER, " +
                    " " + User.PAST_24H_REWARDS + " INTEGER " +
                    ");";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_POOL_TABLE);
        db.execSQL(CREATE_WORKERS_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PRICE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "Upgrading Database from v" + oldVersion + " to v" + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + POOL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WORKERS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PRICE_TABLE_NAME);
        onCreate(db);
    }
}
