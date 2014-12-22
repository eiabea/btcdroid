package com.eiabea.btcdroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.fragments.PayoutFragment;
import com.eiabea.btcdroid.fragments.PoolFragment;
import com.eiabea.btcdroid.fragments.RoundsFragment;
import com.eiabea.btcdroid.fragments.WorkerFragment;
import com.eiabea.btcdroid.model.AvgLuck;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.service.UpdateService;
import com.eiabea.btcdroid.service.UpdateService.UpdateInterface;
import com.eiabea.btcdroid.util.App;

@SuppressLint("InlinedApi")
public class MainActivity extends ActionBarActivity implements UpdateInterface,
        OnPageChangeListener {

    public static final String PAID_APP_PACKAGE = "com.eiabea.paid.btcdroid";
    private static final int BEER_POPUP_THRESHOLD = 10;

    public static final String ACTION_NEW_ROUND_NOTIFICATION = "action_new_round_notification";
    public static final String ACTION_DROP_NOTIFICATION = "action_drop_notification";

    public static final int FRAGMENT_PAYOUT = 0;
    public static final int FRAGMENT_POOL = 1;
    public static final int FRAGMENT_WORKER = 2;
    public static final int FRAGMENT_ROUNDS = 3;

    private static final int INTENT_PREF = 0;
    private static final int INTENT_CUSTOMIZE = 1;
    private static final int INTENT_PARTICIPANTS = 2;

    private static final String STATE_CURRENT_PAGE = "state_current_page";

    // Tiles Layout
    private LinearLayout llTilesHolder;

    // ViewPager Layout
    private ViewPager viewPager;
    private MainViewAdapter adapter;
    private int currentPage = FRAGMENT_POOL;

    private LinearLayout llNoPools;
    private Button btnSetToken;

    private SharedPreferences pref;
    private ClipboardManager clipboard;
    @SuppressWarnings("deprecation")
    private android.text.ClipboardManager clipboardold;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        } else {
            clipboardold = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        }

        currentPage = pref.getInt("userMainFragment", FRAGMENT_POOL);

        reloadData();

    }

    private void handleBeerDialog() {
        if (!BuildConfig.PAID) {
            int appStarts = pref.getInt(BuildConfig.APP_STARTS, 0);

            if (appStarts == BEER_POPUP_THRESHOLD) {
                showBeerDialog();
                pref.edit().putInt(BuildConfig.APP_STARTS, 0).apply();
            } else {
                pref.edit().putInt(BuildConfig.APP_STARTS, ++appStarts).apply();
            }
        }
    }

    private void showBeerDialog() {
        AlertDialog.Builder builder = new Builder(this);
        builder.setTitle("Buy me a beer!");
        builder.setMessage("If you like this app, i would very much appreciate if you buy me a beer by purchasing this app");
        builder.setPositiveButton("PlayStore", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                openPlayStore();
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.create().show();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.currentPage = savedInstanceState.getInt(STATE_CURRENT_PAGE);
    }

    @Override
    protected void onResume() {

        initUi();
        setListeners();

        handleBeerDialog();

        UpdateService.setUpdateInterface(this);
        supportInvalidateOptionsMenu();
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save the user's current game state
        savedInstanceState.putInt(STATE_CURRENT_PAGE, this.currentPage);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(getClass().getSimpleName(), "onNewIntent");
        if (intent != null && intent.getAction() != null) {
            Log.d(getClass().getSimpleName(), "action = " + intent.getAction());
            if (intent.getAction().equalsIgnoreCase(ACTION_DROP_NOTIFICATION)) {
                UpdateService.resetDropNotificationCount();
            } else if (intent.getAction().equalsIgnoreCase(ACTION_NEW_ROUND_NOTIFICATION)) {
                UpdateService.resetNewRoundNotificationCount();
            }
        }
    }

    private void initUi() {

        boolean isLand = getResources().getBoolean(R.bool.is_land);
        boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.app_name_subtitle);
        setSupportActionBar(toolbar);

        adapter = new MainViewAdapter(this, getSupportFragmentManager());
        try {
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

                PagerTitleStrip viewPagerTitle = (PagerTitleStrip) findViewById(R.id.vp_title_main);
                viewPagerTitle.setTextColor(getResources().getColor(R.color.bd_white));

                viewPager = (ViewPager) findViewById(R.id.vp_main);
                viewPager.setOnPageChangeListener(this);
                viewPager.setOffscreenPageLimit(2);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(currentPage);
            }

        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "Something was null, damn! InitUi() (NullPointer)");
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
                        pref.edit().putString(App.PREF_TOKEN, value.toString()).apply();

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

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, PrefsActivity.class), INTENT_PREF);
                break;

            case R.id.action_customize:
                startActivityForResult(new Intent(this, CustomizeActivity.class), INTENT_CUSTOMIZE);
                break;

            case R.id.action_participants:
                startActivityForResult(new Intent(this, ParticipantsActivity.class), INTENT_PARTICIPANTS);
                break;

            case R.id.action_email:
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{App.getResString(R.string.mail_address, MainActivity.this)});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, App.getResString(R.string.mail_subject, MainActivity.this));

                startActivity(Intent.createChooser(emailIntent, App.getResString(R.string.mail_intent_title, MainActivity.this)));
                break;

            case R.id.action_donate:
                final String address = getString(R.string.txt_donations_address);

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.txt_donate_dialog_title));
                builder.setItems(new CharSequence[]{getString(R.string.txt_donate_copy_bitcoin_address), getString(R.string.txt_donate_open_bitcoin_wallet)}, new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                    ClipData clipData = ClipData.newPlainText(getString(R.string.txt_donations), address);
                                    clipboard.setPrimaryClip(clipData);
                                } else {
                                    clipboardold.setText(address);
                                }
                                Toast.makeText(MainActivity.this, address + " " + getString(R.string.txt_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                try {
                                    startActivity(ParticipantsActivity.makeBitcoinIntent(address));
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(MainActivity.this, getString(R.string.toast_donate_no_bitcoin_wallet_found), Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
                break;

            case R.id.action_buy:
                openPlayStore();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPlayStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + PAID_APP_PACKAGE)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + PAID_APP_PACKAGE)));
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent intent) {
        switch (reqCode) {
            case INTENT_PREF:
                if (resCode == RESULT_OK) {

                    // ArrayList<String> changed =
                    // intent.getStringArrayListExtra("changed");

                    // if(changed.contains("price_enabled")||changed.contains("price_source_preference")){
                    App.getInstance().resetPriceEnabled();
                    // }

                    // if(changed.contains("btc_style_preference")||changed.contains("token")){
                    App.getInstance().resetToken();
                    // }

                    // if(changed.contains("price_threshold")){
                    App.getInstance().resetPriceThreshold();
                    // }

                    // if(changed.contains("luck_threshold")){
                    App.getInstance().resetLuckThreshold();
                    // }

                    // if (enabled) {
                    // UpdateService.getInstance().startNotification();
                    // } else {
                    // UpdateService.getInstance().stopNotification();
                    // }

                    // UpdateService.getInstance().startWidgets();
                    // ProfileUpdateService.getInstance().getProfileWidgets();
                    // ProfileUpdateService.getInstance().getStatsWidgets();

                    // TODO Split
                    reloadData();

                }
                break;

            case INTENT_CUSTOMIZE:
                // currentPage = Integer.valueOf(pref.getString("userMainFragment",
                // "0"));
                currentPage = pref.getInt("userMainFragment", 0);
                initUi();
                setListeners();
                break;

            default:
                break;
        }

        super.onActivityResult(reqCode, resCode, intent);
    }

    private void reloadData() {

        if (App.getInstance().isTokenSet()) {
            App.resetUpdateManager(this);
            showInfos();
        } else {
            hideInfos();
        }

    }

    private void showInfos() {
        try {
            boolean isLand = getResources().getBoolean(R.bool.is_land);
            boolean isTablet = getResources().getBoolean(R.bool.is_tablet);

            llNoPools.setVisibility(View.GONE);

            if (isLand && isTablet) {
                llTilesHolder.setVisibility(View.VISIBLE);
            } else {
                viewPager.setVisibility(View.VISIBLE);
            }
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "showInfos --> TabletUi vs. PhoneUi (NullPointer)");
        }
    }

    private void hideInfos() {
        try {
            boolean isLand = getResources().getBoolean(R.bool.is_land);
            boolean isTablet = getResources().getBoolean(R.bool.is_tablet);
            llNoPools.setVisibility(View.VISIBLE);

            if (isLand && isTablet) {
                llTilesHolder.setVisibility(View.INVISIBLE);
            } else {
                viewPager.setVisibility(View.INVISIBLE);
            }
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "showInfos --> TabletUi vs. PhoneUi (NullPointer)");
        }
    }

    private String getFragmentTag(int which) {
        if (which == FRAGMENT_PAYOUT) {
            return PayoutFragment.class.getSimpleName();
        } else if (which == FRAGMENT_POOL) {
            return PoolFragment.class.getSimpleName();
        } else if (which == FRAGMENT_WORKER) {
            return WorkerFragment.class.getSimpleName();
        } else if (which == FRAGMENT_ROUNDS) {
            return RoundsFragment.class.getSimpleName();
        }
        return "";
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

    }

    @Override
    public void onStatsError() {

    }

    @Override
    public void onPricesError() {

    }

    @Override
    public void onAvgLuckError() {

    }

}
