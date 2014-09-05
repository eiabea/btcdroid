package com.eiabea.btcdroid.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.eiabea.btcdroid.util.App;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class Profile implements Parcelable {

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

    // Constructor used for Parcelable
    public Profile(Parcel in) {
        username = in.readString();
        rating = in.readString();
        confirmed_nmc_reward = in.readString();
        send_threshold = in.readString();
        nmc_send_threshold = in.readString();
        confirmed_reward = in.readString();
        wallet = in.readString();
        unconfirmed_nmc_reward = in.readString();
        unconfirmed_reward = in.readString();
        estimated_reward = in.readString();
        hashrate = in.readString();

        workers = new JsonObject();
        workers = App.getInstance().gson.fromJson(in.readString(), JsonObject.class);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(rating);
        dest.writeString(confirmed_nmc_reward);
        dest.writeString(send_threshold);
        dest.writeString(nmc_send_threshold);
        dest.writeString(confirmed_reward);
        dest.writeString(wallet);
        dest.writeString(unconfirmed_nmc_reward);
        dest.writeString(unconfirmed_reward);
        dest.writeString(estimated_reward);
        dest.writeString(hashrate);

        dest.writeString(workers.toString());

    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Profile createFromParcel(Parcel in) {
            return new Profile(in);
        }

        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

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
