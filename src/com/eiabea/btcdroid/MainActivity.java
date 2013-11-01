package com.eiabea.btcdroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.HttpWorker.HttpWorkerInterface;

public class MainActivity extends ActionBarActivity implements OnClickListener, HttpWorkerInterface {

	private static final int INTENT_ADD_POOL = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initUi();
        
        setListeners();
        
        reloadData();
        
    }


    private void getProfile() {
    	showProgress(this, true);
    	App.getInstance().httpWorker.getProfile(this);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_add_pool:
			startActivityForResult(new Intent(this, AddPoolActivity.class), INTENT_ADD_POOL);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {


		default:
			break;
		}
	}
	
	


	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent intent) {
		switch (reqCode) {
		case INTENT_ADD_POOL:
			if(resCode == RESULT_OK){
				App.getInstance().httpWorker.setToken(intent.getExtras().getString("token"));
				
				reloadData();
			}
			break;

		default:
			break;
		}
		
		super.onActivityResult(reqCode, resCode, intent);
	}


	private void reloadData() {
		if(App.getInstance().httpWorker.isTokenSet()){
			getProfile();
		}else{
			Toast.makeText(this, "No Token set", Toast.LENGTH_SHORT).show();
		}
		
	}


	@Override
	public void requestDone(Activity context, Profile profile) {
		ArrayList<Worker> list = profile.getWorkersList();
		
		for(Worker tmp : list){
			Log.d(getClass().getSimpleName(), "Worker: " + tmp.getName() + "; " + tmp.getScore());
			
		}
		showProgress(context, false);
	}

	@Override
	public void requestDone(Activity context, Stats response) {
		// TODO Auto-generated method stub
		
	}

	private void showProgress(Activity context, boolean show){
		((MainActivity)context).setSupportProgressBarIndeterminateVisibility(show);
	}


    
}
