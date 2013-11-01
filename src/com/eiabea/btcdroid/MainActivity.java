package com.eiabea.btcdroid;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.HttpWorker.HttpWorkerInterface;

public class MainActivity extends Activity implements OnClickListener, HttpWorkerInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUi();
        
        setListeners();
        
        App.getInstance().httpWorker.getProfile();
    }


    private void initUi() {
	}


	private void setListeners() {
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


		default:
			break;
		}
	}


	@Override
	public void requestDone(int reqId, Object response) {
		ArrayList<Worker> list = ((Profile)response).getWorkersList();
		
		for(Worker tmp : list){
			Log.d(getClass().getSimpleName(), "Worker: " + tmp.getName() + "; " + tmp.getScore());
			
		}
	}
    
}
