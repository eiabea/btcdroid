package com.eiabea.btcdroid.views;

import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class WorkerView extends LinearLayout {
	
	private Worker worker;
	
	private TextView txtName, txtScore, txtLastShare, txtHashrate, txtShares;

	public WorkerView(Context context) {
		super(context);
		
		initUi();
	}
	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_worker, this, true);

		txtName = (TextView) findViewById(R.id.txt_worker_name);
		
		txtScore = (TextView) findViewById(R.id.txt_worker_score);
		txtLastShare = (TextView) findViewById(R.id.txt_worker_last_share);
		txtHashrate = (TextView) findViewById(R.id.txt_worker_hashrate);
		txtShares = (TextView) findViewById(R.id.txt_worker_shares);
	}
	
	public void setData(Worker worker) {
		this.setWorker(worker);
		
		if(worker != null){
			
			if(worker.isAlive()){
				txtName.setText(worker.getName());
				txtName.setTextColor(getResources().getColor(R.color.bd_black));
				
			}else{
				txtName.setText(worker.getName() + " - inactive");
				txtName.setTextColor(getResources().getColor(R.color.bd_red));
				
			}
			
			txtScore.setText(worker.getScore());
			txtLastShare.setText(App.dateFormat.format(new Date(worker.getLast_share() * 1000)));
			txtHashrate.setText(String.valueOf(worker.getHashrate()) + " MH/s");
			txtShares.setText(String.valueOf(worker.getShares()));
		}
	}
	public Worker getWorker() {
		return worker;
	}
	public void setWorker(Worker worker) {
		this.worker = worker;
	}


}
