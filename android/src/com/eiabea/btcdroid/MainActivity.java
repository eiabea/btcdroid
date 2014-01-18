package com.eiabea.btcdroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.RoundsFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.service.NotificationService;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.HttpWorker.HttpWorkerInterface;

public class MainActivity extends ActionBarActivity implements
		HttpWorkerInterface {

	private static final int INTENT_PREF = 0;

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

	private LinearLayout llNoPools;;
	private Button btnSetToken;

	private boolean isProgessShowing = false;

	private Profile profile = null;
	private Stats stats = null;
	private GenericPrice price = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initUi();

		setListeners();

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			this.profile = savedInstanceState.getParcelable(STATE_PROFILE);
			this.stats = savedInstanceState.getParcelable(STATE_STATS);
			this.price = savedInstanceState.getParcelable(STATE_PRICES);

			setSavedValues();
		} else {
			// String pricesJson =
			// PreferenceManager.getDefaultSharedPreferences(this).getString("pref_prices",
			// "");
			// if (pricesJson != null && pricesJson.length() > 0) {
			// this.prices = App.getInstance().gson.fromJson(pricesJson,
			// PricesMtGox.class);
			// }
			// String statsJson =
			// PreferenceManager.getDefaultSharedPreferences(this).getString("pref_stats",
			// "");
			// if (statsJson != null && statsJson.length() > 0) {
			// this.stats = App.getInstance().gson.fromJson(statsJson,
			// Stats.class);
			// }
			// String profileJson =
			// PreferenceManager.getDefaultSharedPreferences(this).getString("pref_profile",
			// "");
			// if (profileJson != null && profileJson.length() > 0) {
			// this.profile = App.getInstance().gson.fromJson(profileJson,
			// Profile.class);
			// }

			reloadData();

		}

	}

	private void setSavedValues() {
		if (this.price != null) {
			setPrices(this.price);
		}
		if (this.profile != null) {
			setProfile(this.profile);
		}
		if (this.stats != null) {
			setStats(this.stats);
		}
	}

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
		savedInstanceState.putParcelable(STATE_PRICES, this.price);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	private void initUi() {

		getSupportActionBar().setSubtitle(R.string.app_name_subtitle);

		viewPagerTitle = (PagerTitleStrip) findViewById(R.id.vp_title_main);
		viewPagerTitle.setTextColor(getResources().getColor(R.color.bd_white));

		viewPager = (ViewPager) findViewById(R.id.vp_main);
		viewPager.setOffscreenPageLimit(3);

		adapter = new MainViewAdapter(this, getSupportFragmentManager(), this.profile, this.stats, this.price);
		viewPager.setAdapter(adapter);

		llNoPools = (LinearLayout) findViewById(R.id.ll_main_no_pools);
		btnSetToken = (Button) findViewById(R.id.txt_main_set_token);

		if (App.getInstance().isTokenSet()) {
			showInfos();
		} else {
			hideInfos();
		}

	}

	private void setListeners() {
		btnSetToken.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Set an EditText view to get user input 
				final EditText input = new EditText(MainActivity.this);
				new AlertDialog.Builder(MainActivity.this)
			    .setTitle(getString(R.string.alert_set_token_title))
			    .setMessage(getString(R.string.alert_set_token_message))
			    .setView(input)
			    .setPositiveButton(getString(R.string.alert_set_token_ok), new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            Editable value = input.getText(); 
			            
			            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
			            pref.edit().putString(App.PREF_TOKEN, value.toString()).commit();
			            
			            App.getInstance().resetToken();
			            
			            reloadData();
			        }
			    }).setNegativeButton(getString(R.string.alert_set_token_cancel), new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
			        }
			    }).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		itemRefresh = menu.findItem(R.id.action_refresh);

		if (!isProgessShowing && App.getInstance().isTokenSet()) {
			itemRefresh.setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:

			reloadData();

			break;

		case R.id.action_settings:
			startActivityForResult(new Intent(this, PrefsActivity.class), INTENT_PREF);
			break;

		case R.id.action_participants:
			startActivityForResult(new Intent(this, ParticipantsActivity.class), INTENT_PREF);
			break;

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

				NotificationService.getInstance().startInterval();

				reloadData();
			}
			break;

		default:
			break;
		}

		super.onActivityResult(reqCode, resCode, intent);
	}

	private void reloadData() {

		if (App.getInstance().isTokenSet()) {
			App.getInstance().httpWorker.setHttpWorkerInterface(this);
			showInfos();
			showProgress(true);
			App.getInstance().httpWorker.reload();
		} else {
			hideInfos();
		}

	}

	private void showInfos() {
		if (llNoPools.getVisibility() == View.VISIBLE) {
			llNoPools.setVisibility(View.GONE);
		}
		if (viewPager.getVisibility() == View.INVISIBLE) {
			viewPager.setVisibility(View.VISIBLE);
		}
	}

	private void hideInfos() {
		if (llNoPools.getVisibility() == View.GONE) {
			llNoPools.setVisibility(View.VISIBLE);
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
		// Toast.makeText(MainActivity.this, "setProfile",
		// Toast.LENGTH_SHORT).show();
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
		// Toast.makeText(MainActivity.this, "setStats",
		// Toast.LENGTH_SHORT).show();
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

	public void setPrices(GenericPrice price) {
		// Toast.makeText(MainActivity.this, "setPrice",
		// Toast.LENGTH_SHORT).show();
		this.price = price;

		Fragment frag = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_POOL));
		if (frag != null) {
			((PoolFragment) frag).setPrices(price);
		}
	}

	private void handleProgessIndicator() {
		if (pricesLoaded && profileLoaded && statsLoaded) {
			showProgress(false);
			pricesLoaded = profileLoaded = statsLoaded = false;
			// profileLoaded = false;
		}
	}

	@Override
	public void onProfileLoaded(Profile profile) {
		profileLoaded = true;
		setProfile(profile);
		handleProgessIndicator();
	}

	@Override
	public void onStatsLoaded(Stats stats) {
		statsLoaded = true;
		setStats(stats);
		handleProgessIndicator();
	}

	@Override
	public void onPricesLoaded(GenericPrice price) {
		pricesLoaded = true;
		setPrices(price);
		handleProgessIndicator();
	}

}
