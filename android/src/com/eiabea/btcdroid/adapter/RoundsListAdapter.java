package com.eiabea.btcdroid.adapter;

import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.util.App;
import com.eiabea.btcdroid.views.BlockView;
import com.eiabea.btcdroid.views.BlockViewHeader;

public class RoundsListAdapter extends BaseExpandableListAdapter {

	private Context context;
	
	private List<Block> data;
	
	private LayoutInflater inflater;

	public RoundsListAdapter(Context context, List<Block> data) {
		this.context = context;
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
		BlockView holder = (BlockView) convertView;
		
		if(holder == null){
			holder = new BlockView(context);
		}
		
		Block currentBlock = data.get(groupPosition);
		
		holder.setData(currentBlock);
		
		return holder;
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
		BlockViewHeader header = (BlockViewHeader) convertView;

		if (header == null) {
			header = new BlockViewHeader(context);
		}
		
		Block currentBlock = data.get(groupPosition);
		
		header.setData(currentBlock, isExpanded);
		
//		TextView txtDuration = (TextView) header.findViewById(R.id.txt_block_header_duration);
//		TextView txtReward = (TextView) header.findViewById(R.id.txt_block_header_reward);
//		
//		int confirmationsLeft = 100 - currentBlock.getConfirmations();
//		
//		txtDuration.setText(currentBlock.getMining_duration());
//		
//		//Log.d(getClass().getSimpleName(), "current block reward: " + currentBlock.getReward());
//		
//		if(currentBlock.getReward() != null){
//			float reward = Float.valueOf(currentBlock.getReward());
//			txtReward.setText(App.formatReward(reward));
//		}
		

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
