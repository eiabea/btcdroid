package com.eiabea.btcdroid.views;

import java.text.ParseException;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.util.App;

public class BlockView extends LinearLayout {
	
	private Context context;
	
	private Block block;
	
	private TextView txtConfirmations, txtFound, txtStarted;

	public BlockView(Context context) {
		super(context);
		
		this.context = context;
		
		initUi();
	}
	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_block, this, true);
		
		txtConfirmations = (TextView) findViewById(R.id.txt_block_confirmations);
		txtFound = (TextView) findViewById(R.id.txt_block_found);
		txtStarted = (TextView) findViewById(R.id.txt_block_started);
	}
	
	public void setData(Block block) {
		this.setBlock(block);
		
		setConfirmation(txtConfirmations, block);
		
		Date found = null;
		Date started = null;
		
		try {
			found = App.dateStatsFormat.parse(block.getDate_found());
			started = App.dateStatsFormat.parse(block.getDate_started());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(found != null){
			txtFound.setText(App.dateFormat.format(found));
		}
		if(started != null){
			txtStarted.setText(App.dateFormat.format(started));
		}
	}
	
	private void setConfirmation(TextView txt, Block currentBlock) {
		
		int confirmationsLeft = 100 - currentBlock.getConfirmations();
		
		if(confirmationsLeft <= 0){
			txt.setText(R.string.txt_confirmed);
			txt.setTextColor(context.getResources().getColor(R.color.bd_circle_green_solid));
		}else{
			txt.setText(String.valueOf(confirmationsLeft));
			txt.setTextColor(context.getResources().getColor(R.color.bd_orange));
		}
		
	}
	
	public Block getBlock() {
		return block;
	}
	public void setBlock(Block block) {
		this.block = block;
	}

	


}
