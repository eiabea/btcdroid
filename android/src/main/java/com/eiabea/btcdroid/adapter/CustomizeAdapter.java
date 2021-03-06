package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.CustomizeItem;
import com.eiabea.btcdroid.views.CustomizeView;

import java.util.ArrayList;
import java.util.List;

public class CustomizeAdapter extends ArrayAdapter<CustomizeItem> {

    private final Context context;
    private final ArrayList<CustomizeItem> data;

    public CustomizeAdapter(Context context, List<CustomizeItem> objects) {
        super(context, R.layout.list_item_handle_left, objects);
        this.context = context;
        this.data = (ArrayList<CustomizeItem>) objects;

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public CustomizeItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CustomizeView v = (CustomizeView) convertView;

        if (v == null) {
            v = new CustomizeView(context);
        }

        CustomizeItem item = data.get(position);

        v.setData(item);

        return v;

    }

}
