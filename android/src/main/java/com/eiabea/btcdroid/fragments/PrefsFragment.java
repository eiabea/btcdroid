package com.eiabea.btcdroid.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import com.eiabea.btcdroid.PrefsActivity;
import com.eiabea.btcdroid.R;
import com.github.machinarius.preferencefragment.PreferenceFragment;

import java.util.ArrayList;
import java.util.List;

public class PrefsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

//	public static final String KEY_API_TOKEN = "key_api_token";
//	public static final String KEY_PRICE_THRESHOLD = "key_price_threshold";
//	public static final String KEY_LUCK_THRESHOLD = "key_luck_threshold";
//	public static final String KEY_SHOW_PRICE = "key_show_price";
//	public static final String KEY_BTC_STYLE = "key_btc_style";
//	public static final String KEY_NOTIFICATION = "key_notification";

    private List<String> changed;

//	private boolean apiTokenChanged = false;
//	private boolean priceThresholdChanged = false;
//	private boolean luckThresholdChanged = false;
//	private boolean showPriceChanged = false;
//	private boolean btcStyleChanged = false;
//	private boolean notificationChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        changed = new ArrayList<String>();

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        setPricesSource();
        setNotification();
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
        setNotification();

        if (!changed.contains(key)) {
            changed.add(key);
        }

        Intent resultIntent = new Intent();
        resultIntent.putStringArrayListExtra("changed", (ArrayList<String>) changed);
        ((PrefsActivity) getActivity()).setResultIntent(Activity.RESULT_OK, resultIntent);

    }

    private void setPricesSource() {
        boolean priceEnabled = getPreferenceManager().getSharedPreferences().getBoolean("price_enabled", false);
        getPreferenceScreen().findPreference("price_source_preference").setEnabled(priceEnabled);
    }

    private void setNotification() {
        boolean notificationEnabled = getPreferenceManager().getSharedPreferences().getBoolean("notification_enabled", false);
        getPreferenceScreen().findPreference("round_finished_notification_enabled").setEnabled(notificationEnabled);
        getPreferenceScreen().findPreference("notification_hashrate").setEnabled(notificationEnabled);
        getPreferenceScreen().findPreference("notification_sound").setEnabled(notificationEnabled);
        getPreferenceScreen().findPreference("notification_vibrate").setEnabled(notificationEnabled);
        getPreferenceScreen().findPreference("notification_led").setEnabled(notificationEnabled);
    }
}
