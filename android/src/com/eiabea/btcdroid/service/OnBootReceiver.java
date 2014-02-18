package com.eiabea.btcdroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.eiabea.btcdroid.util.App;
 
public class OnBootReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	App.resetUpdateManager(context);
    }
}