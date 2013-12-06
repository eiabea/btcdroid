package com.eiabea.btcdroid.util;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Price;
import com.eiabea.btcdroid.model.Prices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;
 
public class UpdateService extends Service{
 
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Service wurde gestartet", Toast.LENGTH_LONG).show();
        reloadData(false);
    }
 
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service wurde beendet", Toast.LENGTH_LONG).show();
    }
 
	private void reloadData(boolean force) {
		if (App.getInstance().isTokenSet()) {

//				setProfile();
//
//				setStats();

				getPrices();
		}

	}
	
	private void getPrices() {

		App.getInstance().httpWorker.getPrices(new Listener<Prices>() {

			@Override
			public void onResponse(Prices prices) {
				Price currentPrice = App.parsePrices(prices.getData()).getLastPrice();
				Toast.makeText(UpdateService.this, currentPrice.getDisplay_short(), Toast.LENGTH_SHORT).show();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				Toast.makeText(UpdateService.this, App.getResString(R.string.txt_error_loading_price, UpdateService.this), Toast.LENGTH_SHORT).show();

			}
		});
	}
    
}