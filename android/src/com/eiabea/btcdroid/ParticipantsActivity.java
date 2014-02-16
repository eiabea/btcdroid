package com.eiabea.btcdroid;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;

public class ParticipantsActivity extends ActionBarActivity {

	private static ImageView imgQr;

	private RelativeLayout rlQrCodeHolder;

	private ClipboardManager clipboard;

	private LinearLayout llDonationAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_participants);

		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		initUi();

		setListeners();

		showQr(false);
	}

	private void initUi() {

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);

		getSupportActionBar().setSubtitle(R.string.action_participants);

		rlQrCodeHolder = (RelativeLayout) findViewById(R.id.rl_qr_code_holder);

		llDonationAddress = (LinearLayout) findViewById(R.id.ll_donation_address);

		imgQr = (ImageView) findViewById(R.id.img_qr_code);

	}

	private void setListeners() {
		llDonationAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				final String address = getString(R.string.txt_donations_address);
				
				AlertDialog.Builder builder = new AlertDialog.Builder(ParticipantsActivity.this);
				builder.setTitle(getString(R.string.txt_donate_dialog_title));
				builder.setItems(new CharSequence[] { getString(R.string.txt_donate_copy_bitcoin_address), getString(R.string.txt_show_qr_code), getString(R.string.txt_donate_open_bitcoin_wallet) }, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							ClipData clipData = ClipData.newPlainText(getString(R.string.txt_donations), address);
							clipboard.setPrimaryClip(clipData);
							Toast.makeText(ParticipantsActivity.this, address + " " + getString(R.string.txt_copied_to_clipboard), Toast.LENGTH_SHORT).show();
							break;
						case 1:
							showQr(true);

							LoadQrCode task = new LoadQrCode();
							task.execute(new String[] { address });
							break;
						case 2:
							try {
								startActivity(makeBitcoinIntent(address));
							} catch (ActivityNotFoundException e) {
								Toast.makeText(ParticipantsActivity.this, getString(R.string.toast_donate_no_bitcoin_wallet_found), Toast.LENGTH_SHORT).show();
							}
							break;
						}
					}
				});
				builder.create().show();
			}
		});

		rlQrCodeHolder.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				rlQrCodeHolder.setVisibility(View.GONE);
				return true;
			}
		});
	}

	private void showQr(boolean show) {
		if (show) {
			rlQrCodeHolder.setVisibility(View.VISIBLE);
		} else {
			rlQrCodeHolder.setVisibility(View.GONE);
		}

	}

	@Override
	public void onBackPressed() {
		if (rlQrCodeHolder.getVisibility() == View.VISIBLE) {
			showQr(false);
		} else {
			finish();
		}
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

	public static class LoadQrCode extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... wallet) {
			// Encode with a QR Code image
			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(wallet[0], null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 500);
			try {
				Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();

				return bitmap;

			} catch (WriterException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap qr) {
			imgQr.setImageBitmap(qr);

		}
	}

	public static Intent makeBitcoinIntent(String address) {
		// {bitcoin:<address>[?amount=<amount>][?label=<label>][?message=<message>]

		StringBuilder uri = new StringBuilder("bitcoin:");

		uri.append(address);

		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri.toString()));

		return intent;
	}
}
