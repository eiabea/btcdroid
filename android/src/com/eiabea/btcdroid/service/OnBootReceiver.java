package com.eiabea.btcdroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
 
public class OnBootReceiver extends BroadcastReceiver {
 
    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.w(getClass().getSimpleName(), "starting Updateservice...");
        context.startService(new Intent(context, UpdateService.class));
        
		try {
			UpdateService.getInstance().startWidgets();
		} catch (NullPointerException ignore) {
		}
    }
}