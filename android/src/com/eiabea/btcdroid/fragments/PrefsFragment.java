package com.eiabea.btcdroid.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.eiabea.btcdroid.PrefsActivity;
import com.eiabea.btcdroid.R;

public class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        setPricesSource();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		setPricesSource();
		
		Intent resultIntent = new Intent();
		resultIntent.putExtra("key", key);
		((PrefsActivity)getActivity()).setResultIntent(resultIntent);
	}
	
	private void setPricesSource(){
		boolean priceEnabled = getPreferenceManager().getSharedPreferences().getBoolean("price_enabled", false);
		getPreferenceScreen().findPreference("price_source_preference").setEnabled(priceEnabled);
	}
}
