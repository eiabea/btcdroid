package com.eiabea.btcdroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
 
public class OnDeleteReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	if(intent.getAction().equalsIgnoreCase("delete")){
//    		NotificationService.getInstance().clearShown();
    		Log.d(getClass().getSimpleName(), "User dismissed Notification");
    	}
    }
}