package com.eiabea.btcdroid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class WorkerViewHeader extends LinearLayout {

	private Context context;

	private Worker worker;

	private TextView txtName, txtStatus;

	private ImageView imgCircle;

	private FrameLayout flBottomSpacerWhite, flBottomSpacerGrey, flDivider;

	public WorkerViewHeader(Context context) {
		super(context);

		this.context = context;

		initUi();
	}

	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_worker_header, this, true);

		imgCircle = (ImageView) findViewById(R.id.img_worker_header_cirlce);

		txtName = (TextView) findViewById(R.id.txt_worker_name);
		txtStatus = (TextView) findViewById(R.id.txt_worker_status);

		flBottomSpacerWhite = (FrameLayout) findViewById(R.id.fl_worker_bottom_spacer_white);
		flBottomSpacerGrey = (FrameLayout) findViewById(R.id.fl_worker_bottom_spacer_grey);
		flDivider = (FrameLayout) findViewById(R.id.fl_worker_divider);
	}

	public void setData(Worker worker, boolean isExpanded) {
		this.setWorker(worker);

		handleExpand(isExpanded);

		if (worker != null) {
			Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
			Typeface tfItalic = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Italic.ttf");

			boolean showUsername = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("username_workers", true);

			if (showUsername) {
				txtName.setText(worker.getName());
			} else {

				try {

					String fullString = worker.getName();
					String workerName = "";
					int dotIndex = fullString.indexOf(".");
					if (dotIndex > -1) {
						// Increase index to cut out the dot
						dotIndex++;
						workerName = fullString.substring(dotIndex);
						txtName.setText(workerName);
					} else {
						throw new NullPointerException("no Dot");
					}
				} catch (NullPointerException e) {

					txtName.setText(worker.getName());
				}
			}

			if (worker.isAlive()) {
				imgCircle.setImageResource(R.drawable.shape_circle_green);
				txtStatus.setText(App.formatHashRate(worker.getHashrate()));
				txtStatus.setTypeface(tfRegular);
				txtStatus.setTextColor(context.getResources().getColor(R.color.bd_circle_green_solid));
			} else {
				imgCircle.setImageResource(R.drawable.shape_circle_red);
				txtStatus.setText(context.getString(R.string.txt_inactive));
				txtStatus.setTypeface(tfItalic);
				txtStatus.setTextColor(context.getResources().getColor(R.color.bd_circle_red_solid));
			}

		}
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	private void handleExpand(boolean expanded) {
		if (expanded) {
			flBottomSpacerWhite.setVisibility(View.VISIBLE);
			flBottomSpacerGrey.setVisibility(View.GONE);
			flDivider.setVisibility(View.VISIBLE);
		} else {
			flBottomSpacerWhite.setVisibility(View.GONE);
			flBottomSpacerGrey.setVisibility(View.VISIBLE);
			flDivider.setVisibility(View.GONE);
		}
	}

}
