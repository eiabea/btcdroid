package com.eiabea.btcdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.eiabea.btcdroid.fragments.PrefsFragment;

public class PrefsActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
		
//	    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//	    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//	    PrefsFragment fragment = new PrefsFragment();
//	    fragmentTransaction.replace(android.R.id.content fragment);
//	    fragmentTransaction.commit();
		
		initUi();
		
	}

	private void initUi() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		getSupportActionBar().setSubtitle(R.string.txt_settings);
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void setResultIntent(int result, Intent intent){
		setResult(result, intent);
	}

}
