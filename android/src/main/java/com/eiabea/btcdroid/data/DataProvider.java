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
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Pool;
import com.eiabea.btcdroid.model.User;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Set;

public class DataProvider extends ContentProvider {

    public static final String PROVIDER_NAME = BuildConfig.DATA_PROVIDER;
    private static final String TAG = DataProvider.class.getSimpleName();

    private SQLiteDatabase db;

    static final int POOLS = 1;
    static final int POOL_ID = 2;
    static final int WORKERS = 3;
    static final int WORKER_ID = 4;
    static final int PRICES = 5;
    static final int PRICE_ID = 6;
    static final int USERS = 7;
    static final int USER_ID = 8;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.POOL_TABLE_NAME, POOLS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.POOL_TABLE_NAME + "/#", POOL_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.WORKERS_TABLE_NAME, WORKERS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.WORKERS_TABLE_NAME + "/#", WORKER_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PRICE_TABLE_NAME, PRICES);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.PRICE_TABLE_NAME + "/#", PRICE_ID);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.USER_TABLE_NAME, USERS);
        uriMatcher.addURI(PROVIDER_NAME, DatabaseHelper.USER_TABLE_NAME + "/#", USER_ID);
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case POOLS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.POOL_TABLE_NAME;
            case POOL_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.POOL_TABLE_NAME;
            case WORKERS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.WORKERS_TABLE_NAME;
            case WORKER_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.WORKERS_TABLE_NAME;
            case PRICES:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.PRICE_TABLE_NAME;
            case PRICE_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.PRICE_TABLE_NAME;
            case USERS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME + "." + DatabaseHelper.USER_TABLE_NAME;
            case USER_ID:
                return "vnd.android.cursor.item/" + PROVIDER_NAME + "." + DatabaseHelper.USER_TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
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
        SwitchHolder switchHolder = getSwitchHolder(uri);

        if (switchHolder.getId() == -1) {
            qb.setTables(switchHolder.getTable());
        } else {
            qb.setTables(switchHolder.getTable());
            qb.appendWhere(switchHolder.getId() + "=" + uri.getPathSegments().get(0));
        }

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowID;

        SwitchHolder switchHolder = getSwitchHolder(uri);

        rowID = db.insertOrThrow(switchHolder.getTable(), "", values);
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(switchHolder.getContentUri(), rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        SwitchHolder switchHolder = getSwitchHolder(uri);

        if (switchHolder.getId() == -1) {
            count = db.delete(switchHolder.getTable(), selection, selectionArgs);
        } else {
            count = db.delete(switchHolder.getTable(), switchHolder.getId() + " = " + uri.getPathSegments().get(1)
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int count;

        SwitchHolder switchHolder = getSwitchHolder(uri);

        if (switchHolder.getId() == -1) {
            count = db.update(switchHolder.getTable(), values, selection, selectionArgs);
        } else {
            count = db.update(switchHolder.getTable(), values, switchHolder.getId() + " = " + uri.getPathSegments().get(1)
                    + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    public static void insertOrUpdatePool(final Context context, final Pool pool) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String where = null;
                String[] whereArgs = null;


                if (pool != null) {
                    where = Pool._ID + "=?";
                    whereArgs = new String[]{"1"};
                }

                int updated = context.getContentResolver().update(Pool.CONTENT_URI, pool.getContentValues(false),
                        where, whereArgs);

                if (updated == 0) {
                    context.getContentResolver().insert(Pool.CONTENT_URI, pool.getContentValues(true));
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

                if(workers == null){
                    return null;
                }

                Set<Map.Entry<String, JsonElement>> set = workers.entrySet();

                Gson gson = App.getInstance().gson;

                for (Map.Entry<String, JsonElement> current : set) {
                    Worker tmpWorker = gson.fromJson(current.getValue(), Worker.class);

                    String where = null;
                    String[] whereArgs = null;


                    if (tmpWorker != null) {
                        where = Worker.WORKER_NAME + "=?";
                        whereArgs = new String[]{tmpWorker.getWorker_name()};
                    }

                    int updated = context.getContentResolver().update(Worker.CONTENT_URI, tmpWorker.getContentValues(false),
                            where, whereArgs);

                    if (updated == 0) {
                        context.getContentResolver().insert(Worker.CONTENT_URI, tmpWorker.getContentValues(true));
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

    public static void insertOrUpdateUser(final Context context, final User user) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                String where = null;
                String[] whereArgs = null;


                if (user != null) {
                    where = User._ID + "=?";
                    whereArgs = new String[]{"1"};
                }

                int updated = context.getContentResolver().update(User.CONTENT_URI, user.getContentValues(false),
                        where, whereArgs);

                if (updated == 0) {
                    context.getContentResolver().insert(User.CONTENT_URI, user.getContentValues(true));
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

                    where = GenericPrice._ID + "=?";
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

    public static void clearDatabase(Context context) {
        Log.d(TAG, "clearDatabase");
        context.getContentResolver().delete(Pool.CONTENT_URI, null, null);
        context.getContentResolver().delete(Worker.CONTENT_URI, null, null);
        context.getContentResolver().delete(GenericPrice.CONTENT_URI, null, null);
    }

    private SwitchHolder getSwitchHolder(Uri uri) {

        int id = -1;
        String tableName;
        Uri contentUri;

        switch (uriMatcher.match(uri)) {
            case POOLS:
                tableName = DatabaseHelper.POOL_TABLE_NAME;
                contentUri = Pool.CONTENT_URI;
                break;
            case POOL_ID:
                id = POOL_ID;
                tableName = DatabaseHelper.POOL_TABLE_NAME;
                contentUri = Pool.CONTENT_URI;
                break;
            case WORKERS:
                tableName = DatabaseHelper.WORKERS_TABLE_NAME;
                contentUri = Worker.CONTENT_URI;
                break;
            case WORKER_ID:
                id = WORKER_ID;
                tableName = DatabaseHelper.WORKERS_TABLE_NAME;
                contentUri = Worker.CONTENT_URI;
                break;

            case PRICES:
                tableName = DatabaseHelper.PRICE_TABLE_NAME;
                contentUri = GenericPrice.CONTENT_URI;
                break;
            case PRICE_ID:
                id = PRICE_ID;
                tableName = DatabaseHelper.PRICE_TABLE_NAME;
                contentUri = GenericPrice.CONTENT_URI;
                break;

            case USERS:
                tableName = DatabaseHelper.USER_TABLE_NAME;
                contentUri = User.CONTENT_URI;
                break;
            case USER_ID:
                id = USER_ID;
                tableName = DatabaseHelper.USER_TABLE_NAME;
                contentUri = User.CONTENT_URI;
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return new SwitchHolder(id, tableName, contentUri);
    }

    private class SwitchHolder {
        private int id;
        private String table;
        private Uri contentUri;

        public SwitchHolder(int id, String tableName, Uri contentUri) {
            this.id = id;
            this.table = tableName;
            this.contentUri = contentUri;
        }

        public int getId() {
            return id;
        }

        public String getTable() {
            return table;
        }

        public Uri getContentUri() {
            return contentUri;
        }
    }

}
