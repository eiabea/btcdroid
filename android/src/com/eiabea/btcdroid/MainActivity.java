package com.eiabea.btcdroid;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.RoundsFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.UpdateService;

public class MainActivity extends ActionBarActivity {

	private static final int INTENT_PREF = 0;

	public static final String BROADCAST_PRICE = "broadcast_price";
	public static final String BROADCAST_PRICE_PARAM = "prices";

	public static final int FRAGMENT_POOL = 0;
	public static final int FRAGMENT_WORKER = 1;
	public static final int FRAGMENT_ROUNDS = 2;

	private static final String STATE_PROFILE = "state_profile";
	private static final String STATE_STATS = "state_stats";
	private static final String STATE_PRICES = "state_prices";

	private MenuItem itemRefresh;

	private ViewPager viewPager;
	private PagerTitleStrip viewPagerTitle;
	private MainViewAdapter adapter;

	private boolean statsLoaded = false;
	private boolean profileLoaded = false;
	private boolean pricesLoaded = false;

	private TextView txtNoPools;

	private boolean isProgessShowing = false;

	private Profile profile = null;
	private Stats stats = null;
	private PricesMtGox prices = null;

	Messenger mService = null;
	boolean mIsBound;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			this.profile = savedInstanceState.getParcelable(STATE_PROFILE);
			this.stats = savedInstanceState.getParcelable(STATE_STATS);
			this.prices = savedInstanceState.getParcelable(STATE_PRICES);
		} else {
			String pricesJson = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_prices", "");
			if (pricesJson != null && pricesJson.length() > 0) {
				this.prices = App.getInstance().gson.fromJson(pricesJson, PricesMtGox.class);
			}
			String statsJson = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_stats", "");
			if (statsJson != null && statsJson.length() > 0) {
				this.stats = App.getInstance().gson.fromJson(statsJson, Stats.class);
			}
			String profileJson = PreferenceManager.getDefaultSharedPreferences(this).getString("pref_profile", "");
			if (profileJson != null && profileJson.length() > 0) {
				this.profile = App.getInstance().gson.fromJson(profileJson, Profile.class);
			}

		}

		initUi();

		setListeners();

		setSavedValues();

		if(App.getInstance().isTokenSet()){
			showInfos();
		}else{
			hideInfos();
		}
		
		// SERVICE
		// use this to start and trigger a service
		Intent i= new Intent(MainActivity.this, UpdateService.class);
		// potentially add data to the intent
		i.putExtra("KEY1", "Value to be used by the service");
		startService(i); 

		Calendar cal = Calendar.getInstance();

		Intent intent = new Intent(this, UpdateService.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Start every 30 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 30*1000, pintent); 
		
	}

	private void setSavedValues() {
		if (this.prices != null) {
			setPrices(this.prices);
		}
		if (this.profile != null) {
			setProfile(this.profile);
		}
		if (this.stats != null) {
			setStats(this.stats);
		}
	}

//	private void initService() {
//
//		Intent i = new Intent(this, UpdateService.class);
//
//		startService(i);
//
//		doBindService();
//
//	}

	@Override
	protected void onResume() {
		supportInvalidateOptionsMenu();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save the user's current game state
		savedInstanceState.putParcelable(STATE_PROFILE, this.profile);
		savedInstanceState.putParcelable(STATE_STATS, this.stats);
		savedInstanceState.putParcelable(STATE_PRICES, this.prices);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	private void initUi() {

		getSupportActionBar().setSubtitle(R.string.app_name_subtitle);

		viewPagerTitle = (PagerTitleStrip) findViewById(R.id.vp_title_main);
		viewPagerTitle.setTextColor(getResources().getColor(R.color.bd_white));

		viewPager = (ViewPager) findViewById(R.id.vp_main);
		viewPager.setOffscreenPageLimit(3);

		adapter = new MainViewAdapter(this, getSupportFragmentManager(), this.profile, this.stats, this.prices);
		viewPager.setAdapter(adapter);

		txtNoPools = (TextView) findViewById(R.id.txt_main_no_pools);

	}

	private void setListeners() {
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		itemRefresh = menu.findItem(R.id.action_refresh);
		// itemAdd = menu.findItem(R.id.action_add_pool);

		// if (App.getInstance().isTokenSet()) {
		// itemAdd.setVisible(false);
		// } else {
		// itemAdd.setVisible(true);
		// }

		if (!isProgessShowing && App.getInstance().isTokenSet()) {
			itemRefresh.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:

//			showProgress(true);
			
			reloadData();
			
			if(App.getInstance().isTokenSet()){
				showInfos();
			}else{
				hideInfos();
			}
			break;

		case R.id.action_settings:
			startActivityForResult(new Intent(this, PrefsActivity.class), INTENT_PREF);
			break;

		// case R.id.action_participants:
		// startActivityForResult(new Intent(this, ParticipantsActivity.class),
		// INTENT_PREF);
		// break;

		case R.id.action_email:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { App.getResString(R.string.mail_address, MainActivity.this) });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, App.getResString(R.string.mail_subject, MainActivity.this));

			startActivity(Intent.createChooser(emailIntent, App.getResString(R.string.mail_intent_title, MainActivity.this)));
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int reqCode, int resCode, Intent intent) {
		switch (reqCode) {
		case INTENT_PREF:
			if (resCode == RESULT_OK) {
				App.getInstance().resetToken();
				App.getInstance().resetThreshold();
				App.getInstance().resetPriceThreshold();
				App.getInstance().resetPriceEnabled();

				showProgress(true);
				
				if(App.getInstance().isTokenSet()){
					showInfos();
				}else{
					hideInfos();
				}
				
			}
			break;

		default:
			break;
		}

		super.onActivityResult(reqCode, resCode, intent);
	}

	private void reloadData() {

//		showProgress(true);

//		if (App.getInstance().isTokenSet()) {
//			txtNoPools.setVisibility(View.INVISIBLE);
//		}
		// SERVICE
		// use this to start and trigger a service
		Intent i= new Intent(MainActivity.this, UpdateService.class);
		// potentially add data to the intent
		i.putExtra("KEY1", "Value to be used by the service");
		startService(i); 

//		Message msg = new Message();
//		msg.what = 1337;
//		if (mIsBound) {
//			if (mService != null) {
//				try {
//					mService.send(msg);
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}

	}

	private void showInfos() {
		if (txtNoPools.getVisibility() == View.VISIBLE) {
			txtNoPools.setVisibility(View.GONE);
		}
		if (viewPager.getVisibility() == View.INVISIBLE) {
			viewPager.setVisibility(View.VISIBLE);
		}
	}

	private void hideInfos() {
		if (txtNoPools.getVisibility() == View.GONE) {
			txtNoPools.setVisibility(View.VISIBLE);
		}
		if (viewPager.getVisibility() == View.VISIBLE) {
			viewPager.setVisibility(View.INVISIBLE);
		}
	}

	private void showProgress(boolean show) {

		this.isProgessShowing = show;

		if (itemRefresh != null) {
			itemRefresh.setVisible(!show);
		}

		setSupportProgressBarIndeterminateVisibility(show);
	}

	public void setProfile(Profile profile) {
//		Toast.makeText(MainActivity.this, "setProfile", Toast.LENGTH_SHORT).show();
		this.profile = profile;
		Fragment pool = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_POOL));
		if (pool != null) {
			((PoolFragment) pool).setProfile(profile);
		}
		Fragment worker = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_WORKER));
		if (worker != null) {
			((WorkerFragment) worker).setProfile(profile);

		}

	}

	public void setStats(Stats stats) {
//		Toast.makeText(MainActivity.this, "setStats", Toast.LENGTH_SHORT).show();
		this.stats = stats;
		Fragment frag = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_POOL));
		if (frag != null) {
			((PoolFragment) frag).setStats(stats);

		}
		Fragment rounds = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_ROUNDS));
		if (rounds != null) {
			((RoundsFragment) rounds).setStats(stats);

		}
	}

	public void setPrices(PricesMtGox prices) {
//		Toast.makeText(MainActivity.this, "setPrice", Toast.LENGTH_SHORT).show();
		this.prices = prices;

		Fragment frag = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_POOL));
		if (frag != null) {
			((PoolFragment) frag).setPrices(prices);
		}
	}
	

}
