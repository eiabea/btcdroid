package com.eiabea.btcdroid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eiabea.btcdroid.model.Profile;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database specific constant declarations
     */

    public static final String TAG = DatabaseHelper.class.getSimpleName();

    public static final String PROFILE_TABLE_NAME = "profile";

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "btcdroid";

    private static final String CREATE_DIARY_ENTRIES_TABLE =
            " CREATE TABLE " + PROFILE_TABLE_NAME +
                    " (" + Profile._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + Profile.JSON + " TEXT " +
                    ");";

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DIARY_ENTRIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "Upgrading Database from v" + oldVersion + " to v" + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE_NAME);
        onCreate(db);
    }
}
