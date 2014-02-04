package com.eiabea.btcdroid.widget;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.service.ProfileUpdateService;
import com.eiabea.btcdroid.util.App;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class DashClockWidget extends DashClockExtension {
	
	public static final String UPDATE_DASHCLOCK = "update_btc_dashclock";
	
	private DashClockUpdateReceiver mDashClockReceiver;
	
	@Override
	protected void onUpdateData(int reason) {
		
		try{
			ProfileUpdateService.getInstance().getProfileWidgets();
		}catch(Exception ignore){
		}
		
		updateWidget(null);
	
	}
	

	
	@Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);

        LocalBroadcastManager broadcastMgr = LocalBroadcastManager.getInstance(this);
        if (mDashClockReceiver != null) {
            try {
                broadcastMgr.unregisterReceiver(mDashClockReceiver);
            } catch (Exception ignore) {}
        }
        mDashClockReceiver = new DashClockUpdateReceiver();
        broadcastMgr.registerReceiver(mDashClockReceiver, new IntentFilter(UPDATE_DASHCLOCK));
    }
	
	private class DashClockUpdateReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.d(getClass().getSimpleName(), "onReceive");
	    	Profile profile = intent.getParcelableExtra(WidgetProvider.PARAM_PROFILE);
	    	updateWidget(profile);
	    }
	}
	
	private void updateWidget(Profile profile){
		if(profile != null){
			
			ArrayList<Worker> list = profile.getWorkersList();

			int totalHashrate = 0;

			for (Worker tmp : list) {
				totalHashrate += tmp.getHashrate();
			}
			
			Intent i = null;
			PackageManager manager = getPackageManager();
			try {
			    i = manager.getLaunchIntentForPackage(getApplicationContext().getPackageName());
			    if (i == null)
			        throw new PackageManager.NameNotFoundException();
			    i.addCategory(Intent.CATEGORY_LAUNCHER);
			} catch (PackageManager.NameNotFoundException e) {

			}
			
			publishUpdate(new ExtensionData()
			.visible(true)
			.icon(R.drawable.ic_launcher_dashclock)
			.status(App.formatHashRate(totalHashrate))
			.expandedTitle(App.formatHashRate(totalHashrate))
			.expandedBody(getString(R.string.txt_dashclock_expanded_body))
			.clickIntent(i));
		}else{
			publishUpdate(new ExtensionData()
			.visible(true)
			.icon(R.drawable.ic_launcher_dashclock)
			.status("-"));
			
		}
	}
}