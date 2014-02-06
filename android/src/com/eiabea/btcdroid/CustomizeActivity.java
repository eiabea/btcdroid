package com.eiabea.btcdroid;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.eiabea.btcdroid.fragments.CustomizeFragment;

public class CustomizeActivity extends ActionBarActivity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.activity_customize);
		
		CustomizeFragment nav = new CustomizeFragment();
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction trans = manager.beginTransaction();
		trans.replace(R.id.blank, nav, "");
		trans.commit();
		
	}
	
}