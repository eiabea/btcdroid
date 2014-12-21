package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;
import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
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
    private String username;
    private String rating;
    private String confirmed_nmc_reward;
    private String send_threshold;
    private String nmc_send_threshold;
    private String confirmed_reward;
    private String wallet;
    private String unconfirmed_nmc_reward;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getConfirmed_nmc_reward() {
        return confirmed_nmc_reward;
    }

    public void setConfirmed_nmc_reward(String confirmed_nmc_reward) {
        this.confirmed_nmc_reward = confirmed_nmc_reward;
    }

    public String getSend_threshold() {
        return send_threshold;
    }

    public void setSend_threshold(String send_threshold) {
        this.send_threshold = send_threshold;
    }

    public String getNmc_send_threshold() {
        return nmc_send_threshold;
    }

    public void setNmc_send_threshold(String nmc_send_threshold) {
        this.nmc_send_threshold = nmc_send_threshold;
    }

    public String getConfirmed_reward() {
        return confirmed_reward;
    }

    public void setConfirmed_reward(String confirmed_reward) {
        this.confirmed_reward = confirmed_reward;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getUnconfirmed_nmc_reward() {
        return unconfirmed_nmc_reward;
    }

    public void setUnconfirmed_nmc_reward(String unconfirmed_nmc_reward) {
        this.unconfirmed_nmc_reward = unconfirmed_nmc_reward;
    }

    public String getUnconfirmed_reward() {
        return unconfirmed_reward;
    }

    public void setUnconfirmed_reward(String unconfirmed_reward) {
        this.unconfirmed_reward = unconfirmed_reward;
    }

    public String getEstimated_reward() {
        return estimated_reward;
    }

    public void setEstimated_reward(String estimated_reward) {
        this.estimated_reward = estimated_reward;
    }

    public String getHashrate() {
        return hashrate;
    }

    public void setHashrate(String hashrate) {
        this.hashrate = hashrate;
    }

    public JsonObject getWorkers() {
        return workers;
    }

    public void setWorkers(JsonObject workers) {
        this.workers = workers;
    }

    public ArrayList<Worker> getWorkersList() {

        listWorkers = new ArrayList<Worker>();

        Set<Entry<String, JsonElement>> set = workers.entrySet();

        for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext(); ) {
            Entry<String, JsonElement> current = it.next();

            Worker tmpWorker = App.getInstance().gson.fromJson(current.getValue(), Worker.class);
            tmpWorker.setName(current.getKey());

            listWorkers.add(tmpWorker);
        }

        return listWorkers;
    }

}
