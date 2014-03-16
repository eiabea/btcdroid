package com.eiabea.btcdroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
 
public class OnDeleteReceiver extends BroadcastReceiver {
 
	public static final String ACTION_DELETE_DROP = "delete_drop";
	public static final String ACTION_DELETE_NEW_ROUND = "delete_new_round";
	
    @Override
    public void onReceive(Context context, Intent intent) {
    	try{
    		if(intent.getAction().equalsIgnoreCase(ACTION_DELETE_DROP)){
    			UpdateService.resetDropNotificationCount();
    			Log.d(getClass().getSimpleName(), "Drop Notification dismissed");
    		} else if (intent.getAction().equalsIgnoreCase(ACTION_DELETE_NEW_ROUND)){
    			UpdateService.resetNewRoundNotificationCount();
    			Log.d(getClass().getSimpleName(), "New Round Notification dismissed");
    		}
    	}catch(Exception e){
    		Log.e(getClass().getSimpleName(), "Something was null, damn! UpdateService down? (NullPointer)");
    	}
    }

}