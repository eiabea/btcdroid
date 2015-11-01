package com.eiabea.btcdroid;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class ParticipantsActivity extends AppCompatActivity {

    private static ImageView imgQr;

    private static final int QR_CODE_SIZE = 700;

    private RelativeLayout rlQrCodeHolder;

    private ClipboardManager clipboard;
    @SuppressWarnings("deprecation")
    private android.text.ClipboardManager clipboardold;

    private LinearLayout llDonationAddress;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        } else {
            clipboardold = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        }

//		clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        initUi();

        setListeners();

        showQr(false);
    }

    private void initUi() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setSubtitle(R.string.action_participants);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                builder.setItems(new CharSequence[]{getString(R.string.txt_donate_copy_bitcoin_address), getString(R.string.txt_show_qr_code), getString(R.string.txt_donate_open_bitcoin_wallet)}, new DialogInterface.OnClickListener() {
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
//							ClipData clipData = ClipData.newPlainText(getString(R.string.txt_donations), address);
//							clipboard.setPrimaryClip(clipData);
                                Toast.makeText(ParticipantsActivity.this, address + " " + getString(R.string.txt_copied_to_clipboard), Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                showQr(true);

                                LoadQrCode task = new LoadQrCode();
                                task.execute(address);
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

            MultiFormatWriter writer = new MultiFormatWriter();

            try {
                BitMatrix matrix = writer.encode(wallet[0], BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE);

                Bitmap bitmap = Bitmap.createBitmap(QR_CODE_SIZE, QR_CODE_SIZE, Bitmap.Config.ARGB_8888);
                for (int i = 0; i < QR_CODE_SIZE; i++) {
                    for (int j = 0; j < QR_CODE_SIZE; j++) {
                        bitmap.setPixel(i, j, matrix.get(i, j) ? Color.BLACK : Color.WHITE);
                    }
                }

                return bitmap;

            } catch (Exception e) {
                Log.w(getClass().getSimpleName(), "Can't encode QR-Code Bitmap (WriterException)");
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
