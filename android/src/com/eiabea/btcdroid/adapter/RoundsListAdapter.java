package com.eiabea.btcdroid.adapter;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.util.App;

public class RoundsListAdapter extends BaseExpandableListAdapter {

	private List<Block> data;
	
	private LayoutInflater inflater;

	public RoundsListAdapter(Context context, List<Block> data) {
		this.data = data;
		
		Collections.sort(this.data, new App.BlockSorter());
		
		inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return data.get(groupPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		LinearLayout holder = (LinearLayout) convertView;
		
		if(holder == null){
			holder = (LinearLayout) inflater.inflate(R.layout.view_block, null);
		}
		
		Block currentBlock = data.get(groupPosition);
		
		TextView txtConfirmations = (TextView) holder.findViewById(R.id.txt_block_confirmations);
		TextView txtFound = (TextView) holder.findViewById(R.id.txt_block_found);
		TextView txtStarted = (TextView) holder.findViewById(R.id.txt_block_started);
		
		setConfirmation(txtConfirmations, currentBlock);
		
		Date found = null;
		Date started = null;
		
		try {
			found = App.dateStatsFormat.parse(currentBlock.getDate_found());
			started = App.dateStatsFormat.parse(currentBlock.getDate_started());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(found != null){
			txtFound.setText(App.dateFormat.format(found));
		}
		if(started != null){
			txtStarted.setText(App.dateFormat.format(started));
		}
		
		
		return holder;
	}

	private void setConfirmation(TextView txt, Block currentBlock) {
		
		int confirmationsLeft = 100 - currentBlock.getConfirmations();
		
		if(confirmationsLeft < 0){
			txt.setText(R.string.txt_confirmed);
		}else{
			txt.setText(String.valueOf(confirmationsLeft));
		}
		
		
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return data.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if (data != null) {
			return data.size();
		}
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		LinearLayout header = (LinearLayout) convertView;

		if (header == null) {
			header = (LinearLayout) inflater.inflate(R.layout.view_block_header, null);
		}
		
		Block currentBlock = data.get(groupPosition);
		
		TextView txtDuration = (TextView) header.findViewById(R.id.txt_block_header_duration);
		TextView txtReward = (TextView) header.findViewById(R.id.txt_block_header_reward);
		ImageView imgConfirmed = (ImageView) header.findViewById(R.id.img_block_header_confirmed);
		
		int confirmationsLeft = 100 - currentBlock.getConfirmations();
		
		if(confirmationsLeft < 0){
			imgConfirmed.setVisibility(View.VISIBLE);
		}else{
			imgConfirmed.setVisibility(View.INVISIBLE);
		}
		
		txtDuration.setText(currentBlock.getMining_duration());
		
		//Log.d(getClass().getSimpleName(), "current block reward: " + currentBlock.getReward());
		
		if(currentBlock.getReward() != null){
			float reward = Float.valueOf(currentBlock.getReward());
			txtReward.setText(App.formatReward(reward));
		}
		

		return header;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	public class BlockHolder{
		private String title;
		private Block block;
		
		public BlockHolder(String title, Block block) {
			this.title = title;
			this.block = block;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Block getBlock() {
			return block;
		}

		public void setBlock(Block block) {
			this.block = block;
		}

	}

}
