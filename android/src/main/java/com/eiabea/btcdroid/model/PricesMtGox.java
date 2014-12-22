package com.eiabea.btcdroid.model;

import com.google.gson.JsonObject;

public class PricesMtGox {

    // Attributes
    private JsonObject data;

    // Standardconstructor
    public PricesMtGox() {
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

}
