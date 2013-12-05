package com.eiabea.btcdroid.views;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class BlockViewHeader extends LinearLayout {

	private Context context;
	
	private Block block;
	
	private TextView txtDuration, txtReward;
	
	private FrameLayout flBottomSpacerWhite, flBottomSpacerGrey, flDivider;

	public BlockViewHeader(Context context) {
		super(context);
		
		this.context = context;

		initUi();
	}

	private void initUi() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_block_header, this, true);

		txtDuration = (TextView) findViewById(R.id.txt_block_header_duration);
		txtReward = (TextView) findViewById(R.id.txt_block_header_reward);

		flBottomSpacerWhite = (FrameLayout) findViewById(R.id.fl_block_bottom_spacer_white);
		flBottomSpacerGrey = (FrameLayout) findViewById(R.id.fl_block_bottom_spacer_grey);
		flDivider = (FrameLayout) findViewById(R.id.fl_block_divider);
	}

	public void setData(Block block, boolean isExpanded) {
		this.setBlock(block);
		
		handleExpand(isExpanded);

		if (block != null) {

			txtDuration.setText(block.getMining_duration());
			
			setReward(txtReward, block);
			
		}
	}
	
	private void setReward(TextView txt, Block currentBlock) {
		
		if(block.getReward() != null){
			float reward = Float.valueOf(block.getReward());
			int confirmationsLeft = 100 - currentBlock.getConfirmations();

			txt.setText(App.formatReward(reward));
			
			if(confirmationsLeft <= 0){
				txt.setTextColor(context.getResources().getColor(R.color.bd_circle_green_solid));
			}else{
				txt.setTextColor(context.getResources().getColor(R.color.bd_actionbar_background));
			}
		}else{
			txt.setText("n.A.");
			txt.setTextColor(context.getResources().getColor(R.color.bd_circle_red_solid));
		}
		
		
		
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

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

}
