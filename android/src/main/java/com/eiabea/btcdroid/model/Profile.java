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
    private String send_threshold;
    private String confirmed_reward;
    private String unconfirmed_reward;
    private String estimated_reward;
    private String hashrate;
    private JsonObject workers;
    private ArrayList<Worker> listWorkers;

    // Constructor used for Parcelable
    public Profile(Parcel in) {
        send_threshold = in.readString();
        confirmed_reward = in.readString();
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
        dest.writeString(send_threshold);
        dest.writeString(confirmed_reward);
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

        Set<Entry<String, JsonElement>> set = getWorkers().entrySet();

        for (Iterator<Entry<String, JsonElement>> it = set.iterator(); it.hasNext(); ) {
            Entry<String, JsonElement> current = it.next();

            Worker tmpWorker = App.getInstance().gson.fromJson(current.getValue(), Worker.class);
            tmpWorker.setName(current.getKey());

            listWorkers.add(tmpWorker);
        }

        return listWorkers;
    }

}
