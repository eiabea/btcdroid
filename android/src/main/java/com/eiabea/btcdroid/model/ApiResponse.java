package com.eiabea.btcdroid.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.eiabea.btcdroid.data.DataProvider;
import com.eiabea.btcdroid.data.DatabaseHelper;
import com.google.gson.JsonObject;

import org.json.JSONObject;

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

    public Pool getPool() {
        return pool;
    }
}