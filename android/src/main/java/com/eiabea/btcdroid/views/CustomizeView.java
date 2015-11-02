package com.eiabea.btcdroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.CustomizeItem;

public class CustomizeView extends LinearLayout {

//	private Context context;

    private CustomizeItem item;

    private TextView txtName;

    public CustomizeView(Context context) {
        super(context);

//		this.context = context;

        initUi();
    }

    private void initUi() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_handle_left, this, true);

        txtName = (TextView) findViewById(R.id.text);
    }

    public void setData(final CustomizeItem item) {
        this.setItem(item);

        txtName.setText(item.getName());

    }

    public CustomizeItem getItem() {
        return item;
    }

    private void setItem(CustomizeItem item) {
        this.item = item;
    }

}
