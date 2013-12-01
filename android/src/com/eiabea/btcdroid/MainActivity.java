package com.eiabea.btcdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.RoundsFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;
import com.eiabea.btcdroid.model.Prices;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.util.App;

public class MainActivity extends ActionBarActivity {

	private static final int INTENT_PREF = 0;

	public static final int FRAGMENT_POOL = 0;
	public static final int FRAGMENT_WORKER = 1;
	public static final int FRAGMENT_ROUNDS = 2;

	private static final String STATE_PROFILE = "state_profile";
	private static final String STATE_STATS = "state_stats";
	private static final String STATE_PRICES = "state_prices";

	private MenuItem itemRefresh;

	private ViewPager viewPager;
	private MainViewAdapter adapter;

	private boolean statsLoaded = false;
	private boolean profileLoaded = false;
	private boolean pricesLoaded = false;

	private TextView txtNoPools;

	private boolean isProgessShowing = false;

	private Profile profile = null;
	private Stats stats = null;
	private Prices prices = null;

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
			this.prices = savedInstanceState.getParcelable(STATE_PRICES);
		}

		reloadData(false);

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
		savedInstanceState.putParcelable(STATE_PRICES, this.prices);

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
	}

	private void getProfile() {

		profileLoaded = false;

		App.getInstance().httpWorker.getProfile(new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {

				setProfile(profile);

				profileLoaded = true;

				readyLoading();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				profileLoaded = true;

				Toast.makeText(MainActivity.this, App.getResString(R.string.txt_error_loading_profile, MainActivity.this), Toast.LENGTH_SHORT).show();

				readyLoading();
			}
		});
	}

	private void getStats() {

		statsLoaded = false;

		App.getInstance().httpWorker.getStats(new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {

				setStats(stats);

				statsLoaded = true;

				readyLoading();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				statsLoaded = true;

				Toast.makeText(MainActivity.this, App.getResString(R.string.txt_error_loading_stats, MainActivity.this), Toast.LENGTH_SHORT).show();

				Log.e(getClass().getSimpleName(), error.getMessage() + error.getCause());

				readyLoading();
			}
		});
	}

	private void getPrices() {

		pricesLoaded = false;

		App.getInstance().httpWorker.getPrices(new Listener<Prices>() {

			@Override
			public void onResponse(Prices prices) {

				setPrices(prices);

				pricesLoaded = true;

				readyLoading();
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {

				pricesLoaded = true;

				Toast.makeText(MainActivity.this, App.getResString(R.string.txt_error_loading_price, MainActivity.this), Toast.LENGTH_SHORT).show();

				readyLoading();
			}
		});
	}

	private void readyLoading() {
		if (App.isPriceEnabled) {

			if (profileLoaded == true && statsLoaded == true && pricesLoaded == true) {
				showInfos();
				showProgress(false);
			}
		} else {
			if (profileLoaded == true && statsLoaded == true) {
				showInfos();
				showProgress(false);
			}

		}
	}

	private void initUi() {

		getSupportActionBar().setSubtitle(R.string.app_name_subtitle);

		viewPager = (ViewPager) findViewById(R.id.vp_main);
		// TODO improve!
		// Current: Main --> Data --> Fragment
		// Future: Fragment --> Data from Main
		viewPager.setOffscreenPageLimit(3);

		adapter = new MainViewAdapter(this, getSupportFragmentManager());
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
		// case R.id.action_add_pool:
		// startActivityForResult(new Intent(this, PrefsActivity.class),
		// INTENT_PREF);
		// break;

		case R.id.action_refresh:
			reloadData(true);
			break;

		case R.id.action_settings:
			startActivityForResult(new Intent(this, PrefsActivity.class), INTENT_PREF);
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

				reloadData(true);

			}
			break;

		default:
			break;
		}

		super.onActivityResult(reqCode, resCode, intent);
	}

	private void reloadData(boolean force) {
		if (App.getInstance().isTokenSet()) {

			txtNoPools.setVisibility(View.INVISIBLE);

			if (this.profile == null || force) {
				showProgress(true);

				getProfile();
			} else {
				setProfile(profile);
			}

			if (this.stats == null || force) {
				showProgress(true);

				getStats();
			} else {
				setStats(stats);
			}

			if (this.prices == null || force && App.isPriceEnabled) {
				showProgress(true);

				getPrices();
			} else {
				setPrices(prices);
			}
		} else {
			hideInfos();
		}

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

	public void updateCurrentTotalHashrate(int hashrate) {
		Fragment pool = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_POOL));
		if (pool != null) {
			((PoolFragment) pool).updateCurrentTotalHashrate(hashrate);
		}
	}

	public void setProfile(Profile profile) {
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

	public void setPrices(Prices prices) {
		this.prices = prices;
		Fragment frag = (getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.vp_main + ":" + FRAGMENT_POOL));
		if(frag != null){
			((PoolFragment) frag).setPrices(prices);
		}

	}

}
