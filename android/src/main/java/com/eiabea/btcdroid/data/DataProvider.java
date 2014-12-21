package com.eiabea.btcdroid.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.eiabea.btcdroid.BuildConfig;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

public class DataProvider extends ContentProvider {

    public static final String PROVIDER_NAME = BuildConfig.DATA_PROVIDER;
    private static final String TAG = DataProvider.class.getSimpleName();

    private SQLiteDatabase db;

    static final int PROFILES = 1;
    static final int PROFILE_ID = 2;
    static final int STATS = 3;
    static final int STAT_ID = 4;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PROFILE_TABLE_NAME, PROFILES);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PROFILE_TABLE_NAME + "/#", PROFILE_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.STATS_TABLE_NAME, STATS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.STATS_TABLE_NAME + "/#", STAT_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        /**
         * Create a write able database which will trigger its creation if it
         * doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db != null);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {

            case PROFILES:
                qb.setTables(DatabaseHelper.PROFILE_TABLE_NAME);
                break;
            case PROFILE_ID:
                qb.setTables(DatabaseHelper.PROFILE_TABLE_NAME);
                qb.appendWhere(PROFILE_ID + "=" + uri.getPathSegments().get(0));
                break;

            case STATS:
                qb.setTables(DatabaseHelper.STATS_TABLE_NAME);
                break;
            case STAT_ID:
                qb.setTables(DatabaseHelper.STATS_TABLE_NAME);
                qb.appendWhere(STAT_ID + "=" + uri.getPathSegments().get(0));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case PROFILES:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.PROFILE_TABLE_NAME;
            case PROFILE_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.PROFILE_TABLE_NAME;
            case STATS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.STATS_TABLE_NAME;
            case STAT_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.STATS_TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = 0;

        switch (uriMatcher.match(uri)) {

            case PROFILES:
            case PROFILE_ID:
                rowID = db.insertOrThrow(DatabaseHelper.PROFILE_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Profile.CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;

            case STATS:
            case STAT_ID:
                rowID = db.insertOrThrow(DatabaseHelper.STATS_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Stats.CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;

            default:
                break;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;
        String id;

        switch (uriMatcher.match(uri)) {

            case PROFILES:
                count = db.delete(DatabaseHelper.PROFILE_TABLE_NAME, selection, selectionArgs);
                break;
            case PROFILE_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.PROFILE_TABLE_NAME, PROFILE_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case STATS:
                count = db.delete(DatabaseHelper.STATS_TABLE_NAME, selection, selectionArgs);
                break;
            case STAT_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.STATS_TABLE_NAME, STAT_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        String id;

        switch (uriMatcher.match(uri)) {
            case PROFILES:
                count = db.update(DatabaseHelper.PROFILE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case PROFILE_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.PROFILE_TABLE_NAME, values, PROFILE_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case STATS:
                count = db.update(DatabaseHelper.STATS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case STAT_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.STATS_TABLE_NAME, values, STAT_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    public static void insertOrUpdateProfile(final Context context, final Profile profile) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String where = null;
                String[] whereArgs = null;


                if (profile != null) {
                    profile.setJson(App.getInstance().gson.toJson(profile));

                    where = Profile._ID + "=?";
                    whereArgs = new String[]{ "1" };
                }

                int updated = context.getContentResolver().update(Profile.CONTENT_URI, profile.getContentValues(false),
                        where, whereArgs);

                if (updated == 0) {
                    context.getContentResolver().insert(Profile.CONTENT_URI, profile.getContentValues(true));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute();
    }

    public static void insertOrUpdateStats(final Context context, final Stats stats) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String where = null;
                String[] whereArgs = null;


                if (stats != null) {
                    stats.setJson(App.getInstance().gson.toJson(stats));

                    where = Stats._ID + "=?";
                    whereArgs = new String[]{ "1" };
                }

                int updated = context.getContentResolver().update(Stats.CONTENT_URI, stats.getContentValues(false),
                        where, whereArgs);

                if (updated == 0) {
                    context.getContentResolver().insert(Stats.CONTENT_URI, stats.getContentValues(true));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute();
    }


    public static void clearDatabase(Context context) {
        Log.d(TAG, "clearDatabase");
        context.getContentResolver().delete(Profile.CONTENT_URI, null, null);
        context.getContentResolver().delete(Stats.CONTENT_URI, null, null);
    }

}
