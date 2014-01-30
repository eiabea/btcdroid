package com.eiabea.btcdroid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.fragments.PayoutFragment;
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
		HttpWorkerInterface, OnPageChangeListener {

	private static final int INTENT_PREF = 0;

	public static final int FRAGMENT_PAYOUT = 0;
	public static final int FRAGMENT_POOL = 1;
	public static final int FRAGMENT_WORKER = 2;
	public static final int FRAGMENT_ROUNDS = 3;

	private static final String STATE_PROFILE = "state_profile";
	private static final String STATE_STATS = "state_stats";
	private static final String STATE_PRICES = "state_prices";
	private static final String STATE_PROFILE_LOADED = "state_profile_loaded";
	private static final String STATE_STATS_LOADED = "state_stats_loaded";
	private static final String STATE_PRICES_LOADED = "state_prices_loaded";
	private static final String STATE_CURRENT_PAGE = "state_current_page";
	private static final String STATE_PROGRESS_SHOWING = "state_progress_showing";

	private MenuItem itemRefresh;

	// Tiles Layout
	private LinearLayout llTilesHolder;

	// ViewPager Layout
	private ViewPager viewPager;
	private PagerTitleStrip viewPagerTitle;
	private MainViewAdapter adapter;
	private int currentPage = FRAGMENT_POOL;

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

		initUi();
		setListeners();

		if (savedInstanceState == null) {
			reloadData();
		}

	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		this.profile = savedInstanceState.getParcelable(STATE_PROFILE);
		this.stats = savedInstanceState.getParcelable(STATE_STATS);
		this.price = savedInstanceState.getParcelable(STATE_PRICES);
		this.currentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE);
		this.isProgessShowing = savedInstanceState.getBoolean(STATE_PROGRESS_SHOWING);
		this.profileLoaded = savedInstanceState.getBoolean(STATE_PROFILE_LOADED);
		this.statsLoaded = savedInstanceState.getBoolean(STATE_STATS_LOADED);
		this.pricesLoaded = savedInstanceState.getBoolean(STATE_PRICES_LOADED);

		setSavedValues();
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

		handleProgessIndicator();

	}

	@Override
	protected void onResume() {
		supportInvalidateOptionsMenu();
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save the user's current game state
		savedInstanceState.putParcelable(STATE_PROFILE, this.profile);
		savedInstanceState.putParcelable(STATE_STATS, this.stats);
		savedInstanceState.putParcelable(STATE_PRICES, this.price);
		savedInstanceState.putInt(STATE_CURRENT_PAGE, this.currentPage);
		savedInstanceState.putBoolean(STATE_PROGRESS_SHOWING, this.isProgessShowing);
		savedInstanceState.putBoolean(STATE_PROFILE_LOADED, this.profileLoaded);
		savedInstanceState.putBoolean(STATE_STATS_LOADED, this.statsLoaded);
		savedInstanceState.putBoolean(STATE_PRICES_LOADED, this.pricesLoaded);

		// Always call the superclass so it can save the view hierarchy state
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		// NotificationService.getInstance().clearShown();
	}

	private void initUi() {

		boolean isLand = getResources().getBoolean(R.bool.is_land);
		boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

		setContentView(R.layout.activity_main);

		getSupportActionBar().setSubtitle(R.string.app_name_subtitle);

		adapter = new MainViewAdapter(this, getSupportFragmentManager(), this.profile, this.stats, this.price);
		if (isTablet && isLand) {

			llTilesHolder = (LinearLayout) findViewById(R.id.ll_tiles_holder);

			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ft.replace(R.id.fl_pool_tile, adapter.getItem(FRAGMENT_POOL), getFragmentTag(FRAGMENT_POOL));
			ft.replace(R.id.fl_payout_tile, adapter.getItem(FRAGMENT_PAYOUT), getFragmentTag(FRAGMENT_PAYOUT));
			ft.replace(R.id.fl_worker_tile, adapter.getItem(FRAGMENT_WORKER), getFragmentTag(FRAGMENT_WORKER));
			ft.replace(R.id.fl_round_tile, adapter.getItem(FRAGMENT_ROUNDS), getFragmentTag(FRAGMENT_ROUNDS));
			ft.commitAllowingStateLoss();
		} else {
			Log.d(getClass().getSimpleName(), "viewpager");

			viewPagerTitle = (PagerTitleStrip) findViewById(R.id.vp_title_main);
			viewPagerTitle.setTextColor(getResources().getColor(R.color.bd_white));

			viewPager = (ViewPager) findViewById(R.id.vp_main);
			// viewPager.setOffscreenPageLimit(2);
			viewPager.setOnPageChangeListener(this);

			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(currentPage);
		}

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
				new AlertDialog.Builder(MainActivity.this).setTitle(getString(R.string.alert_set_token_title)).setMessage(getString(R.string.alert_set_token_message)).setView(input).setPositiveButton(getString(R.string.alert_set_token_ok), new DialogInterface.OnClickListener() {
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

				boolean enabled = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("notification_enabled", false);

				if (enabled) {
					NotificationService.getInstance().startInterval();
				} else {
					NotificationService.getInstance().stopInterval();
				}

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
			pricesLoaded = profileLoaded = statsLoaded = false;
			App.getInstance().httpWorker.setHttpWorkerInterface(this);
			showInfos();
			handleProgessIndicator();
			// showProgress(true);
			App.getInstance().httpWorker.reload();
		} else {
			hideInfos();
		}

	}

	private void showInfos() {
		try {
			boolean isLand = getResources().getBoolean(R.bool.is_land);
			boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
			// if (llNoPools.getVisibility() == View.VISIBLE) {
			llNoPools.setVisibility(View.GONE);
			// }
			// if (viewPager.getVisibility() == View.INVISIBLE) {
			if (isLand && isTablet) {
				llTilesHolder.setVisibility(View.VISIBLE);

			} else {
				viewPager.setVisibility(View.VISIBLE);

			}
			// }
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void hideInfos() {
		try {
			boolean isLand = getResources().getBoolean(R.bool.is_land);
			boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
			// if (llNoPools.getVisibility() == View.VISIBLE) {
			llNoPools.setVisibility(View.VISIBLE);
			// }
			// if (viewPager.getVisibility() == View.INVISIBLE) {
			if (isLand && isTablet) {
				llTilesHolder.setVisibility(View.INVISIBLE);

			} else {
				viewPager.setVisibility(View.INVISIBLE);

			}
			// }
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void showProgress(boolean show) {

		Log.w(getClass().getSimpleName(), "showProgress: " + show);

		setSupportProgressBarIndeterminateVisibility(show);

		if (itemRefresh != null) {
			itemRefresh.setVisible(!show);
		}

		this.isProgessShowing = show;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
		try {
			((PayoutFragment) getFragment(FRAGMENT_PAYOUT)).setProfile(profile);
			((PoolFragment) getFragment(FRAGMENT_POOL)).setProfile(profile);
			((WorkerFragment) getFragment(FRAGMENT_WORKER)).setProfile(profile);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void setStats(Stats stats) {
		this.stats = stats;
		try {
			((PoolFragment) getFragment(FRAGMENT_POOL)).setStats(stats);
			((RoundsFragment) getFragment(FRAGMENT_ROUNDS)).setStats(stats);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void setPrices(GenericPrice price) {
		this.price = price;
		try {
			((PayoutFragment) getFragment(FRAGMENT_PAYOUT)).setPrices(price);

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private Fragment getFragment(int which) {
		Fragment frag = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + which));
		if (frag != null) {
			return frag;
		} else {
			frag = getSupportFragmentManager().findFragmentByTag(getFragmentTag(which));
			if (frag != null) {
				return frag;
			}
		}
		return null;
	}

	private String getFragmentTag(int which) {
		switch (which) {
		case FRAGMENT_PAYOUT:
			return PayoutFragment.class.getSimpleName();
		case FRAGMENT_POOL:
			return PoolFragment.class.getSimpleName();
		case FRAGMENT_WORKER:
			return WorkerFragment.class.getSimpleName();
		case FRAGMENT_ROUNDS:
			return RoundsFragment.class.getSimpleName();
		}
		return "";
	}

	private void handleProgessIndicator() {
		Log.w(getClass().getSimpleName(), "price: " + pricesLoaded);
		Log.w(getClass().getSimpleName(), "profile: " + profileLoaded);
		Log.w(getClass().getSimpleName(), "stats: " + statsLoaded);
		if (pricesLoaded && profileLoaded && statsLoaded) {
			showProgress(false);
			if (adapter == null) {
				adapter = new MainViewAdapter(this, getSupportFragmentManager(), this.profile, this.stats, this.price);
			} else {
				adapter.setProfile(profile);
				adapter.setStats(stats);
				adapter.setPrice(price);
			}

			try {
				viewPager.setAdapter(adapter);
				viewPager.setCurrentItem(currentPage);

			} catch (NullPointerException e) {

			}
		} else {
			showProgress(true);
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

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		currentPage = arg0;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		initUi();
		setListeners();

	}

	@Override
	public void onProfileError() {
		profileLoaded = true;
		handleProgessIndicator();
		Toast.makeText(MainActivity.this, getString(R.string.toast_error_loading_profile), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatsError() {
		statsLoaded = true;
		handleProgessIndicator();
		Toast.makeText(MainActivity.this, getString(R.string.toast_error_loading_stats), Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPricesError() {
		pricesLoaded = true;
		handleProgessIndicator();
		Toast.makeText(MainActivity.this, getString(R.string.toast_error_loading_price), Toast.LENGTH_SHORT).show();
	}

}
