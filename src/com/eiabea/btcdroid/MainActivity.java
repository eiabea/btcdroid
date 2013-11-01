package com.eiabea.btcdroid;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.GsonRequest;
import com.eiabea.btcdroid.util.HttpWorker;

public class MainActivity extends Activity implements OnClickListener {

	private Button btnGetProfile;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUi();
        
        setListeners();
    }


    private void initUi() {
		btnGetProfile = (Button) findViewById(R.id.btn_main_get);
	}


	private void setListeners() {
		btnGetProfile.setOnClickListener(this);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_main_get:
			getProfile();
			getStats();
			break;

		default:
			break;
		}
	}


	private void getProfile() {
		Log.d(getClass().getSimpleName(), "get Profile");
		
		String url = HttpWorker.PROFILE_URL;
		
		System.out.println(HttpWorker.mQueue.toString());
		
		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, new Response.Listener<Profile>() {
			@Override
			public void onResponse(Profile response) {
				
				Log.d(getClass().getSimpleName(), "get profile done");
				
				ArrayList<Worker> list = response.getWorkersList();
				
				for(Worker tmp : list){
					Log.d(getClass().getSimpleName(), "Worker: " + tmp.getName() + "; " + tmp.getScore());
					
				}
				
			}
		}, new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getSimpleName(), "Error while loading: " + error.getMessage());
			}
		}));
		
	}
	
	private void getStats() {
		Log.d(getClass().getSimpleName(), "get Stats");
		
		String url = HttpWorker.STATS_URL;
		
		System.out.println(HttpWorker.mQueue.toString());
		
		HttpWorker.mQueue.add(new GsonRequest<Stats>(url, Stats.class, null, new Response.Listener<Stats>() {
			@Override
			public void onResponse(Stats response) {
				
				Log.d(getClass().getSimpleName(), "get stats done");
				
				Log.d(getClass().getSimpleName(), "round_duration: " + response.getRound_duration());
				
				
				
			}
		}, new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getSimpleName(), "Error while loading: " + error.getMessage());
			}
		}));
		
	}

    
}
