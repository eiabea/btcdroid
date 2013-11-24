package com.eiabea.btcdroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;

public class WorkerViewHeader extends LinearLayout {

	private Worker worker;

	private TextView txtName;

	public WorkerViewHeader(Context context) {
		super(context);

		initUi();
	}

	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_worker_header, this, true);

		txtName = (TextView) findViewById(R.id.txt_worker_name);

	}

	public void setData(Worker worker) {
		this.setWorker(worker);

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

}
