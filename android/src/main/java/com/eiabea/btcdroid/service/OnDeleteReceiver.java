package com.eiabea.btcdroid.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OnDeleteReceiver extends BroadcastReceiver {

    public static final String ACTION_DELETE_DROP = "delete_drop";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equalsIgnoreCase(ACTION_DELETE_DROP)) {
                UpdateService.resetDropNotificationCount();
                Log.d(getClass().getSimpleName(), "Drop Notification dismissed");
            }
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Something was null, damn! UpdateService down? (NullPointer)");
        }
    }

}