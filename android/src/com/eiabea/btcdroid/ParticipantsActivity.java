package com.eiabea.btcdroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.jwetherell.quick_response_code.data.Contents;
import com.jwetherell.quick_response_code.qrcode.QRCodeEncoder;

public class ParticipantsActivity extends ActionBarActivity {

	private ImageView imgQr;
	
	private LinearLayout llParticipantsHolder;
	private RelativeLayout rlQrCodeHolder;
	
	private ClipboardManager clipboard;
	
	private TextView txtZanglAddress;
	private Button btnZanglAddress;
	
	private Dialog inputBlocker;

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
		
		llParticipantsHolder = (LinearLayout) findViewById(R.id.ll_participants_holder);
		rlQrCodeHolder = (RelativeLayout) findViewById(R.id.rl_qr_code_holder);
		
		txtZanglAddress = (TextView) findViewById(R.id.txt_zangl_address);
		btnZanglAddress = (Button) findViewById(R.id.btn_zangl_address);
		
		imgQr = (ImageView) findViewById(R.id.img_qr_code);

		inputBlocker = new Dialog(ParticipantsActivity.this, android.R.style.Theme_Panel);
		inputBlocker.setCancelable(true);
	}

	private void setListeners() {
		txtZanglAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ClipData clip = ClipData.newPlainText("zanglDonation", txtZanglAddress.getText().toString());
				clipboard.setPrimaryClip(clip);
				Toast.makeText(ParticipantsActivity.this, "Address copied to Clipboard", Toast.LENGTH_SHORT).show();
				
			}
		});
		btnZanglAddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showQr(true);
				
				LoadQrCode task = new LoadQrCode();
			    task.execute(new String[] { "http://www.vogella.com" });
			}
		});
	}
	
	private void showQr(boolean show){
		if(show){
			inputBlocker.show();
//			llParticipantsHolder.setVisibility(View.GONE);
			rlQrCodeHolder.setVisibility(View.VISIBLE);
		}else{
			inputBlocker.dismiss();
//			llParticipantsHolder.setVisibility(View.VISIBLE);
			rlQrCodeHolder.setVisibility(View.GONE);
			
		}
			
	}
	
	

//	@Override
//	public void onBackPressed() {
//		if(inputBlocker.isShowing()){
//			showQr(false);
//		}else{
//			finish();
//		}
//	}



	private class LoadQrCode extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... wallet) {
			// Encode with a QR Code image
			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(wallet[0], null, Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), 350);
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
}
