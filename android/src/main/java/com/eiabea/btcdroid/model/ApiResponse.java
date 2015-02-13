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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class ApiResponse {
    private User user;
    private JsonObject workers;
    private Pool pool;

    // Standardconstructor
    public ApiResponse() {
    }

    public User getUser() {
        return user;
    }

    public JsonObject getWorkers() {
        return workers;
    }

    public ArrayList<Worker> getWorkersAsArraylist(){
        Set<Map.Entry<String, JsonElement>> set = workers.entrySet();
        ArrayList<Worker> workersList = new ArrayList<>();

        Gson gson = App.getInstance().gson;

        for (Map.Entry<String, JsonElement> current : set) {
            workersList.add(gson.fromJson(current.getValue(), Worker.class));
        }

        return workersList;
    }

    public Pool getPool() {
        return pool;
    }
}