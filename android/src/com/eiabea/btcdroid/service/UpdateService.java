package com.eiabea.btcdroid.service;

import java.text.ParseException;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.model.PricesBTCe;
import com.eiabea.btcdroid.model.PricesBitStamp;
import com.eiabea.btcdroid.model.PricesMtGox;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Stats;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.util.GsonRequest;
import com.eiabea.btcdroid.util.HttpWorker;

public class UpdateService extends Service {

	private SharedPreferences pref;

	public static final String PARAM_GET = "param_get";

	public static final int GET_PRICE = 0;
	public static final int GET_STATS = 1;
	public static final int GET_PROFILE = 2;

	public static final int PRICE_SOURCE_BITSTAMP_USD = 0;
	public static final int PRICE_SOURCE_MTGOX_USD = 1;
	public static final int PRICE_SOURCE_MTGOX_EUR = 2;
	public static final int PRICE_SOURCE_BTCE_USD = 3;
	public static final int PRICE_SOURCE_BTCE_EUR = 4;

	private Profile profile;
	private Stats stats;
	private GenericPrice price;

	private static UpdateService me;
	private static UpdateInterface updateInterface;

	// private ScheduledExecutorService scheduleWidgets;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		me = this;

		pref = PreferenceManager.getDefaultSharedPreferences(this);

		Log.d(getClass().getSimpleName(), "onCreate");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		try {
			int get = intent.getIntExtra(PARAM_GET, -1);

			switch (get) {
			case GET_PROFILE:
				getProfileWidgets();
				break;
			case GET_STATS:
				getStatsWidgets();
				break;
			case GET_PRICE:
				getPriceWidgets();
				break;

			default:
				getProfileWidgets();
				getPriceWidgets();
				getStatsWidgets();
				break;
			}

		} catch (NullPointerException e) {
			Log.d(getClass().getSimpleName(), "Start Service without data");
		}
		return START_NOT_STICKY;
	}

	public static UpdateService getInstance() {
		return me;
	}

	// public void start() {
	// startNotification();
	// startWidgets();
	// }
	//
	// public void startNotification() {
	// if (scheduleNotification != null) {
	// scheduleNotification.shutdownNow();
	// }
	//
	// int intervalNotificaion =
	// Integer.valueOf(pref.getString("notification_interval", "60"));
	//
	// scheduleNotification = Executors.newSingleThreadScheduledExecutor();
	//
	// scheduleNotification.scheduleAtFixedRate(new Runnable() {
	// public void run() {
	// if (App.getInstance().isTokenSet()) {
	// getProfileNotification();
	// } else {
	// Log.d(getClass().getSimpleName(), "No Token set");
	// }
	// }
	// }, intervalNotificaion, intervalNotificaion, TimeUnit.MINUTES);
	// }
	//
	// public void startWidgets() {
	// if (scheduleWidgets != null) {
	// scheduleWidgets.shutdownNow();
	// }
	//
	// int intervalWidget = Integer.valueOf(pref.getString("widget_interval",
	// "30"));
	// scheduleWidgets = Executors.newSingleThreadScheduledExecutor();
	//
	// scheduleWidgets.scheduleAtFixedRate(new Runnable() {
	// public void run() {
	// if (App.getInstance().isTokenSet()) {
	// getProfileWidgets();
	// getStatsWidgets();
	// getPriceWidgets();
	// } else {
	// Log.d(getClass().getSimpleName(), "No Token set");
	// }
	// }
	// }, 0, intervalWidget, TimeUnit.MINUTES);
	// }
	//
	// public void stop() {
	// stopNotification();
	// stopWidgets();
	// }
	//
	// public void stopNotification() {
	// if (scheduleNotification != null) {
	// scheduleNotification.shutdownNow();
	// }
	// }
	//
	// public void stopWidgets() {
	// if (scheduleWidgets != null) {
	// scheduleWidgets.shutdownNow();
	// }
	// }

	private void getProfileWidgets() {
		Log.d(getClass().getSimpleName(), "get Profile Widgets");

		String url = HttpWorker.PROFILE_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null, new Listener<Profile>() {

			@Override
			public void onResponse(Profile profile) {
				Log.d(getClass().getSimpleName(), "onResponse Profile Widgets");
				handleDropNotification(profile);
				onProfileLoaded(profile);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getSimpleName(), "onErrorResponse Profile Widgets");
				Log.d(getClass().getSimpleName(), " " + error.getCause());
				onProfileError();
			}
		}));

	}

	private void getStatsWidgets() {
		Log.d(getClass().getSimpleName(), "get Stats Widgets");

		String url = HttpWorker.STATS_URL + PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN, "");

		System.out.println(HttpWorker.mQueue.toString());

		HttpWorker.mQueue.add(new GsonRequest<Stats>(url, Stats.class, null, new Listener<Stats>() {

			@Override
			public void onResponse(Stats stats) {
				Log.d(getClass().getSimpleName(), "onResponse Stats Widgets");
				handleRoundFinishedNotification(stats);
				onStatsLoaded(stats);
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d(getClass().getSimpleName(), "onErrorResponse Stats Widgets");
				Log.d(getClass().getSimpleName(), " " + error.getCause());
				onStatsError();
			}
		}));
	}

	private void getPriceWidgets() {
		Log.d(getClass().getSimpleName(), "get Price Widgets");

		Log.d(getClass().getSimpleName(), "Getting Prices");

		int source = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("price_source_preference", "0"));

		switch (source) {
		case PRICE_SOURCE_BITSTAMP_USD:
			App.getInstance().httpWorker.getPricesBitStamp(new Listener<PricesBitStamp>() {

				@Override
				public void onResponse(PricesBitStamp prices) {

					try {
						GenericPrice price = new GenericPrice();
						price.setValueFloat(Float.parseFloat(prices.getLast()));
						price.setSource(getApplicationContext().getString(R.string.BitStamp_short));
						price.setSymbol("$");

						onPriceLoaded(price);
					} catch (NullPointerException ignore) {
						onPriceError();
					}

				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					onPriceError();

				}
			});
			break;
		case PRICE_SOURCE_MTGOX_USD:

			App.getInstance().httpWorker.getPricesMtGox("USD", new Listener<PricesMtGox>() {

				@Override
				public void onResponse(PricesMtGox prices) {

					try {
						// Write jsonData to PricesMtGox Object
						GenericPrice price = prices.getLastPrice();
						price.setSource(getApplicationContext().getString(R.string.MtGox_short));
						price.setSymbol("$");

						onPriceLoaded(price);
					} catch (NullPointerException ignore) {
						onPriceError();
					}

				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					onPriceError();
				}
			});
			break;
		case PRICE_SOURCE_MTGOX_EUR:

			App.getInstance().httpWorker.getPricesMtGox("EUR", new Listener<PricesMtGox>() {

				@Override
				public void onResponse(PricesMtGox prices) {

					try {
						// Write jsonData to PricesMtGox Object
						prices = App.parsePrices(prices.getData());

						GenericPrice price = prices.getLastPrice();
						price.setSource(getApplicationContext().getString(R.string.MtGox_short));
						price.setSymbol("€");

						onPriceLoaded(price);
					} catch (NullPointerException ignore) {
						onPriceError();
					}
				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					onPriceError();

				}
			});
			break;
		case PRICE_SOURCE_BTCE_USD:

			App.getInstance().httpWorker.getPricesBTCe("USD", new Listener<PricesBTCe>() {

				@Override
				public void onResponse(PricesBTCe prices) {
					try {
						GenericPrice price = new GenericPrice();
						price.setValueFloat(prices.getTicker().getLast());
						price.setSource(getApplicationContext().getString(R.string.BTCe_short));
						price.setSymbol("$");

						onPriceLoaded(price);
					} catch (NullPointerException e) {
						onPriceError();
					}
				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					onPriceError();
				}
			});
			break;
		case PRICE_SOURCE_BTCE_EUR:

			App.getInstance().httpWorker.getPricesBTCe("EUR", new Listener<PricesBTCe>() {

				@Override
				public void onResponse(PricesBTCe prices) {

					try {
						GenericPrice price = new GenericPrice();
						price.setValueFloat(prices.getTicker().getLast());
						price.setSource(getApplicationContext().getString(R.string.BTCe_short));
						price.setSymbol("€");

						onPriceLoaded(price);
					} catch (NullPointerException e) {
						onPriceError();
					}
				}

			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					onPriceError();
				}
			});
			break;

		default:
			break;
		}
	}

	// public void getProfileNotification() {
	// Log.d(getClass().getSimpleName(), "get Profile Notification");
	//
	// String url = HttpWorker.PROFILE_URL +
	// PreferenceManager.getDefaultSharedPreferences(this).getString(App.PREF_TOKEN,
	// "");
	//
	// System.out.println(HttpWorker.mQueue.toString());
	//
	// HttpWorker.mQueue.add(new GsonRequest<Profile>(url, Profile.class, null,
	// new Listener<Profile>() {
	//
	// @Override
	// public void onResponse(Profile response) {
	// List<Worker> workers = response.getWorkersList();
	//
	// int totalHashrate = 0;
	// int limit = Integer.valueOf(pref.getString("notification_hashrate",
	// "0"));
	//
	// for (Worker tmp : workers) {
	// totalHashrate += tmp.getHashrate();
	// }
	//
	// boolean enabled =
	// PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notification_enabled",
	// false);
	// if (limit > 0 && totalHashrate < limit && enabled) {
	// createFirstDropNotification(totalHashrate);
	// }
	// Log.d(getClass().getSimpleName(), "onResponse Notification");
	// }
	// }, new ErrorListener() {
	//
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// Log.d(getClass().getSimpleName(), "onErrorResponse Notification");
	// Log.d(getClass().getSimpleName(), " " + error.getCause());
	// Intent i = new Intent(getApplicationContext(),
	// TotalHashrateWidgetProvider.class);
	// i.setAction(TotalHashrateWidgetProvider.LOADING_FAILED);
	// getApplicationContext().sendBroadcast(i);
	// }
	// }));
	//
	// }

	private void handleRoundFinishedNotification(Stats stats) {
		boolean globalEnabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notification_enabled", false);
		boolean enabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("round_finished_notification_enabled", false);
		if (enabled && globalEnabled) {
			try {
				SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				long lastDuration = pref.getLong("lastDuration", 0);
				long duration = App.dateDurationFormat.parse(stats.getRound_duration()).getTime();
				if (lastDuration > duration) {
					createRoundFinishedNotification();
				}
				pref.edit().putLong("lastDuration", duration).commit();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}

	private void createRoundFinishedNotification() {
		// if (!alreadyShown) {

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_round_finished).setContentTitle(getString(R.string.txt_new_round_title)).setContentText(getString(R.string.txt_new_round_message)).setAutoCancel(true);

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		mBuilder.setContentIntent(pi);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = setFinishParameters(mBuilder.build());

		mNotificationManager.notify(0, notif);

	}

	private void handleDropNotification(Profile profile) {
		boolean enabled = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("notification_enabled", false);
		try {

			if (enabled) {

				List<Worker> workers = profile.getWorkersList();

				int totalHashrate = 0;
				int limit = Integer.valueOf(pref.getString("notification_hashrate", "0"));

				for (Worker tmp : workers) {
					totalHashrate += tmp.getHashrate();
				}

				if (limit > 0 && totalHashrate < limit) {

					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(getString(R.string.txt_hashrate_dropped_title)).setContentText(String.format(getString(R.string.txt_hashrate_dropped_message), App.formatHashRate(totalHashrate))).setAutoCancel(true);

					Intent deleteIntent = new Intent(this, OnDeleteReceiver.class);
					deleteIntent.setAction("delete");
					PendingIntent pendingDeleteIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, deleteIntent, 0);

					Intent intent = new Intent(this, MainActivity.class);
					PendingIntent pi = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

					mBuilder.setContentIntent(pi);
					mBuilder.setDeleteIntent(pendingDeleteIntent);
					NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					Notification notif = setDefaults(mBuilder.build());

					mNotificationManager.notify(0, notif);
				}
			}
		} catch (NullPointerException e) {
			Log.e(getClass().getSimpleName(), "Something was null, damn! (NullPointer)");
		}

	}

	private Notification setDefaults(Notification notification) {
		boolean sound = pref.getBoolean("notification_sound", true);
		boolean vibrate = pref.getBoolean("notification_vibrate", false);
		boolean led = pref.getBoolean("notification_led", false);

		if (sound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}

		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		if (led) {
			notification.ledARGB = 0xffff8b00;
			notification.ledOnMS = 300;
			notification.ledOffMS = 1000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}

		return notification;
	}

	private Notification setFinishParameters(Notification notification) {
		boolean sound = pref.getBoolean("notification_sound", true);
		boolean vibrate = pref.getBoolean("notification_vibrate", false);
		boolean led = pref.getBoolean("notification_led", false);

		if (sound) {
			notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tada);
		}

		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		if (led) {
			notification.ledARGB = 0xffff8b00;
			notification.ledOnMS = 300;
			notification.ledOffMS = 1000;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		}

		return notification;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Stats getStats() {
		return stats;
	}

	public void setStats(Stats stats) {
		this.stats = stats;
	}

	public GenericPrice getPrice() {
		return price;
	}

	public void setPrice(GenericPrice price) {
		this.price = price;
	}

	public static UpdateInterface getUpdateInterface() {
		return updateInterface;
	}

	public static void setUpdateInterface(UpdateInterface updateInterface) {
		UpdateService.updateInterface = updateInterface;
	}

	private void onPriceLoaded(GenericPrice price) {
		this.price = price;
		App.updateWidgets(getApplicationContext(), price);

		if (updateInterface != null) {
			updateInterface.onPricesLoaded(price);
		}
	}

	private void onPriceError() {
		GenericPrice price = null;
		App.updateWidgets(getApplicationContext(), price);
		if (updateInterface != null) {
			updateInterface.onPricesError();
		}
	}

	private void onProfileLoaded(Profile profile) {
		UpdateService.this.profile = profile;
		App.updateWidgets(getApplicationContext(), profile);
		if (updateInterface != null) {
			updateInterface.onProfileLoaded(profile);
		}
	}

	private void onProfileError() {
		Profile profile = null;
		App.updateWidgets(getApplicationContext(), profile);
		if (updateInterface != null) {
			updateInterface.onProfileError();
		}
	}

	private void onStatsLoaded(Stats stats) {
		UpdateService.this.stats = stats;
		App.updateWidgets(getApplicationContext(), stats);
		if (updateInterface != null) {
			updateInterface.onStatsLoaded(stats);
		}
	}

	private void onStatsError() {
		Stats stats = null;
		App.updateWidgets(getApplicationContext(), stats);
		if (updateInterface != null) {
			updateInterface.onStatsError();
		}
	}

	public interface UpdateInterface {
		public void onProfileLoaded(Profile profile);

		public void onProfileError();

		public void onStatsLoaded(Stats stats);

		public void onStatsError();

		public void onPricesLoaded(GenericPrice price);

		public void onPricesError();
	};

}
