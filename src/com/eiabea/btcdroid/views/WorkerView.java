package com.eiabea.btcdroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;

public class WorkerView extends LinearLayout {
	
	private Worker worker;
	
	private TextView txtName, txtScore;

	public WorkerView(Context context) {
		super(context);
		
		initUi();
	}
	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_worker, this, true);

		txtName = (TextView) findViewById(R.id.txt_worker_name);
		
		txtScore = (TextView) findViewById(R.id.txt_worker_score);
	}
	
	public void setData(Worker worker) {
		this.setWorker(worker);
		
		if(worker != null){
			txtName.setText(worker.getName());
			txtScore.setText(worker.getScore());
		}
	}
	public Worker getWorker() {
		return worker;
	}
	public void setWorker(Worker worker) {
		this.worker = worker;
	}


}
