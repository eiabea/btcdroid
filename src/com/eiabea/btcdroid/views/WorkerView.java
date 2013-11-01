package com.eiabea.btcdroid.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Worker;

public class WorkerView extends LinearLayout {
	
	private Worker worker;

	public WorkerView(Context context) {
		super(context);
		
		initUi();
	}
	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_event_list_item, this, true);

		txtHeader = (TextView) findViewById(R.id.txt_event_list_item_header);
		
		txtTitle = (TextView) findViewById(R.id.txt_event_list_item_title);
//		txtSubtitle = (TextView) findViewById(R.id.txt_event_list_item_subtitle);

		txtDateNumber = (FontFitTextView) findViewById(R.id.txt_event_list_item_date_number);
		txtDateText = (TextView) findViewById(R.id.txt_event_list_item_date_text);
	}
	
	public void setData(Worker worker) {
		this.setWorker(worker);
		
		if(worker != null){
			txtTitle.setText(event.geteTitle());
//			txtSubtitle.setText(event.geteSubtitle());
			
			Date d = new Date(event.geteDateFrom() * 1000);
			
			Calendar c = Calendar.getInstance(Locale.getDefault());
			c.setTime(d);
			
			txtDateNumber.setText(String.valueOf(c.get(Calendar.DAY_OF_MONTH)));
			txtDateText.setText(getFormatedMonth(c.get(Calendar.MONTH)));
			
			if(showHeader){
				txtHeader.setVisibility(View.VISIBLE);
			}else{
				txtHeader.setVisibility(View.GONE);
				
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
