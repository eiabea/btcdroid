package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;
import com.eiabea.btcdroid.util.App;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

public class Profile {

    private static final String URL = "content://" + DataProvider.PROVIDER_NAME + "/" + DatabaseHelper.PROFILE_TABLE_NAME;
    public static final Uri CONTENT_URI = Uri.parse(URL);

    public static final String _ID = "_id";
    public static final String JSON = "json";

    // Database
    private long id;
    private String json;

    // Attributes
    private String send_threshold;
    private String confirmed_reward;
    private String unconfirmed_reward;
    private String estimated_reward;
    private String hashrate;
    private JsonObject workers;
    private ArrayList<Worker> listWorkers;


    // Standardconstructor
    public Profile() {
    }

    public Profile(Cursor c) {
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


    public String getSend_threshold() {
        return send_threshold;
    }

    public String getConfirmed_reward() {
        return confirmed_reward;
    }

    public String getUnconfirmed_reward() {
        return unconfirmed_reward;
    }

    public String getEstimated_reward() {
        return estimated_reward;
    }

    public String getHashrate() {
        return hashrate;
    }

    public JsonObject getWorkers() {
        return workers;
    }

    public ArrayList<Worker> getWorkersList() {

        listWorkers = new ArrayList<Worker>();

        Set<Entry<String, JsonElement>> set = workers.entrySet();

        Gson gson = App.getInstance().gson;

        for (Entry<String, JsonElement> current : set) {
            Worker tmpWorker = gson.fromJson(current.getValue(), Worker.class);
            tmpWorker.setName(current.getKey());

            listWorkers.add(tmpWorker);
        }

        return listWorkers;
    }

}
