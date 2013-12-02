package com.eiabea.btcdroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;

public class WorkerViewHeader extends LinearLayout {

	private Worker worker;

	private TextView txtName;
	
	private FrameLayout flBottomSpacerWhite, flBottomSpacerGrey, flDivider;

	public WorkerViewHeader(Context context) {
		super(context);

		initUi();
	}

	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_worker_header, this, true);

		txtName = (TextView) findViewById(R.id.txt_worker_name);

		flBottomSpacerWhite = (FrameLayout) findViewById(R.id.fl_worker_bottom_spacer_white);
		flBottomSpacerGrey = (FrameLayout) findViewById(R.id.fl_worker_bottom_spacer_grey);
		flDivider = (FrameLayout) findViewById(R.id.fl_worker_divider);
	}

	public void setData(Worker worker, boolean isExpanded) {
		this.setWorker(worker);
		
		handleExpand(isExpanded);

		if (worker != null) {

			if (worker.isAlive()) {
				txtName.setText(worker.getName());
				txtName.setTextColor(getResources().getColor(R.color.bd_black));

			} else {
				txtName.setText(worker.getName() + " - inactive");
				txtName.setTextColor(getResources().getColor(R.color.bd_red));

			}
		}
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}
	
	private void handleExpand(boolean expanded){
		if(expanded){
			flBottomSpacerWhite.setVisibility(View.VISIBLE);
			flBottomSpacerGrey.setVisibility(View.GONE);
			flDivider.setVisibility(View.VISIBLE);
		}else{
			flBottomSpacerWhite.setVisibility(View.GONE);
			flBottomSpacerGrey.setVisibility(View.VISIBLE);
			flDivider.setVisibility(View.GONE);
		}
	}

}
