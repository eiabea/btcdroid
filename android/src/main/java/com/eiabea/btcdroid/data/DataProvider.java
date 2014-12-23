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
import com.eiabea.btcdroid.model.AvgLuck;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataProvider extends ContentProvider {

    public static final String PROVIDER_NAME = BuildConfig.DATA_PROVIDER;
    private static final String TAG = DataProvider.class.getSimpleName();

    private SQLiteDatabase db;

    static final int PROFILES = 1;
    static final int PROFILE_ID = 2;
    static final int STATS = 3;
    static final int STAT_ID = 4;
    static final int PRICES = 5;
    static final int PRICE_ID = 6;
    static final int AVG_LUCKS = 7;
    static final int AVG_LUCK_ID = 8;
    static final int WORKERS = 9;
    static final int WORKER_ID = 10;
    static final int ROUNDS = 11;
    static final int ROUND_ID = 12;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PROFILE_TABLE_NAME, PROFILES);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PROFILE_TABLE_NAME + "/#", PROFILE_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.STATS_TABLE_NAME, STATS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.STATS_TABLE_NAME + "/#", STAT_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PRICE_TABLE_NAME, PRICES);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PRICE_TABLE_NAME + "/#", PRICE_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.AVG_LUCK_TABLE_NAME, AVG_LUCKS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.AVG_LUCK_TABLE_NAME + "/#", AVG_LUCK_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.WORKER_TABLE_NAME, WORKERS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.WORKER_TABLE_NAME + "/#", WORKER_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.ROUNDS_TABLE_NAME, ROUNDS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.ROUNDS_TABLE_NAME + "/#", ROUND_ID);
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

            case PRICES:
                qb.setTables(DatabaseHelper.PRICE_TABLE_NAME);
                break;
            case PRICE_ID:
                qb.setTables(DatabaseHelper.PRICE_TABLE_NAME);
                qb.appendWhere(PRICE_ID + "=" + uri.getPathSegments().get(0));
                break;

            case AVG_LUCKS:
                qb.setTables(DatabaseHelper.AVG_LUCK_TABLE_NAME);
                break;
            case AVG_LUCK_ID:
                qb.setTables(DatabaseHelper.AVG_LUCK_TABLE_NAME);
                qb.appendWhere(AVG_LUCK_ID + "=" + uri.getPathSegments().get(0));
                break;

            case WORKERS:
                qb.setTables(DatabaseHelper.WORKER_TABLE_NAME);
                break;
            case WORKER_ID:
                qb.setTables(DatabaseHelper.WORKER_TABLE_NAME);
                qb.appendWhere(WORKER_ID + "=" + uri.getPathSegments().get(0));
                break;

            case ROUNDS:
                qb.setTables(DatabaseHelper.ROUNDS_TABLE_NAME);
                break;
            case ROUND_ID:
                qb.setTables(DatabaseHelper.ROUNDS_TABLE_NAME);
                qb.appendWhere(ROUND_ID + "=" + uri.getPathSegments().get(0));
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
            case PRICES:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.PRICE_TABLE_NAME;
            case PRICE_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.PRICE_TABLE_NAME;
            case AVG_LUCKS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.AVG_LUCK_TABLE_NAME;
            case AVG_LUCK_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.AVG_LUCK_TABLE_NAME;
            case WORKERS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.WORKER_TABLE_NAME;
            case WORKER_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.WORKER_TABLE_NAME;
            case ROUNDS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.ROUNDS_TABLE_NAME;
            case ROUND_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.ROUNDS_TABLE_NAME;
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

            case PRICES:
            case PRICE_ID:
                rowID = db.insertOrThrow(DatabaseHelper.PRICE_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(GenericPrice.CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;

            case AVG_LUCKS:
            case AVG_LUCK_ID:
                rowID = db.insertOrThrow(DatabaseHelper.AVG_LUCK_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(AvgLuck.CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;

            case WORKERS:
            case WORKER_ID:
                rowID = db.insertOrThrow(DatabaseHelper.WORKER_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Worker.CONTENT_URI, rowID);
                    getContext().getContentResolver().notifyChange(_uri, null);
                    return _uri;
                }
                break;

            case ROUNDS:
            case ROUND_ID:
                rowID = db.insertOrThrow(DatabaseHelper.ROUNDS_TABLE_NAME, "", values);

                if (rowID > 0) {
                    Uri _uri = ContentUris.withAppendedId(Block.CONTENT_URI, rowID);
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

            case PRICES:
                count = db.delete(DatabaseHelper.PRICE_TABLE_NAME, selection, selectionArgs);
                break;
            case PRICE_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.PRICE_TABLE_NAME, PRICE_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case AVG_LUCKS:
                count = db.delete(DatabaseHelper.AVG_LUCK_TABLE_NAME, selection, selectionArgs);
                break;
            case AVG_LUCK_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.AVG_LUCK_TABLE_NAME, AVG_LUCK_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case WORKERS:
                count = db.delete(DatabaseHelper.WORKER_TABLE_NAME, selection, selectionArgs);
                break;
            case WORKER_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.WORKER_TABLE_NAME, WORKER_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case ROUNDS:
                count = db.delete(DatabaseHelper.ROUNDS_TABLE_NAME, selection, selectionArgs);
                break;
            case ROUND_ID:
                id = uri.getPathSegments().get(1);
                count = db.delete(DatabaseHelper.ROUNDS_TABLE_NAME, ROUND_ID + " = " + id
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

            case PRICES:
                count = db.update(DatabaseHelper.PRICE_TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRICE_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.PRICE_TABLE_NAME, values, PRICE_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case AVG_LUCKS:
                count = db.update(DatabaseHelper.AVG_LUCK_TABLE_NAME, values, selection, selectionArgs);
                break;
            case AVG_LUCK_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.AVG_LUCK_TABLE_NAME, values, AVG_LUCK_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case WORKERS:
                count = db.update(DatabaseHelper.WORKER_TABLE_NAME, values, selection, selectionArgs);
                break;
            case WORKER_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.WORKER_TABLE_NAME, values, WORKER_ID + " = " + id
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            case ROUNDS:
                count = db.update(DatabaseHelper.ROUNDS_TABLE_NAME, values, selection, selectionArgs);
                break;
            case ROUND_ID:
                id = uri.getPathSegments().get(1);
                count = db.update(DatabaseHelper.ROUNDS_TABLE_NAME, values, ROUND_ID + " = " + id
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
                    whereArgs = new String[]{"1"};
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
                    whereArgs = new String[]{"1"};
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

    public static void insertOrUpdatePrice(final Context context, final GenericPrice price) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String where = null;
                String[] whereArgs = null;


                if (price != null) {
                    price.setJson(App.getInstance().gson.toJson(price));

                    where = Stats._ID + "=?";
                    whereArgs = new String[]{"1"};
                }

                int updated = context.getContentResolver().update(GenericPrice.CONTENT_URI, price.getContentValues(false),
                        where, whereArgs);

                if (updated == 0) {
                    context.getContentResolver().insert(GenericPrice.CONTENT_URI, price.getContentValues(true));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute();
    }

    public static void insertOrUpdateAvgLuck(final Context context, final AvgLuck avgLuck) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String where = null;
                String[] whereArgs = null;


                if (avgLuck != null) {
                    avgLuck.setJson(App.getInstance().gson.toJson(avgLuck));

                    where = Stats._ID + "=?";
                    whereArgs = new String[]{"1"};
                }

                int updated = context.getContentResolver().update(AvgLuck.CONTENT_URI, avgLuck.getContentValues(false),
                        where, whereArgs);

                if (updated == 0) {
                    context.getContentResolver().insert(AvgLuck.CONTENT_URI, avgLuck.getContentValues(true));
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute();
    }

    public static void insertOrUpdateWorkers(final Context context, final JsonObject workers) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                ArrayList<Worker> listWorkers = new ArrayList<Worker>();

                Set<Map.Entry<String, JsonElement>> set = workers.entrySet();

                Gson gson = App.getInstance().gson;

                for (Map.Entry<String, JsonElement> current : set) {
                    Worker tmpWorker = gson.fromJson(current.getValue(), Worker.class);
                    tmpWorker.setName(current.getKey().replace(".", ""));

                    listWorkers.add(tmpWorker);
                }

                for (Worker worker : listWorkers) {

                    String where = null;
                    String[] whereArgs = null;

                    where = Worker.NAME + "=?";
                    whereArgs = new String[]{worker.getName()};

                    int updated = context.getContentResolver().update(Worker.CONTENT_URI, worker.getContentValues(false),
                            where, whereArgs);

                    if (updated == 0) {
                        context.getContentResolver().insert(Worker.CONTENT_URI, worker.getContentValues(true));
                    }
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }

        }.execute();
    }

    public static void insertOrUpdateRounds(final Context context, final JsonObject blocks) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                List<Block> listBlocks = new ArrayList<Block>();

                Set<Map.Entry<String, JsonElement>> set = blocks.entrySet();

                Gson gson = App.getInstance().gson;

                for (Map.Entry<String, JsonElement> current : set) {

                    Block tmpBlock = gson.fromJson(current.getValue(), Block.class);
                    tmpBlock.setNumber(Long.parseLong(current.getKey()));

                    listBlocks.add(tmpBlock);
                }

                for (Block block : listBlocks) {

                    String where = null;
                    String[] whereArgs = null;

                    where = Block.NUMBER + "=?";
                    whereArgs = new String[]{String.valueOf(block.getNumber())};

                    int updated = context.getContentResolver().update(Block.CONTENT_URI, block.getContentValues(false),
                            where, whereArgs);

                    if (updated == 0) {
                        context.getContentResolver().insert(Block.CONTENT_URI, block.getContentValues(true));
                    }
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
        context.getContentResolver().delete(GenericPrice.CONTENT_URI, null, null);
        context.getContentResolver().delete(AvgLuck.CONTENT_URI, null, null);
        context.getContentResolver().delete(Worker.CONTENT_URI, null, null);
        context.getContentResolver().delete(Block.CONTENT_URI, null, null);
    }

}
