package com.eiabea.btcdroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
 
public class OnBootReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	Intent service = new Intent(context, ProfileUpdateService.class);
        context.startService(service);
    }
}