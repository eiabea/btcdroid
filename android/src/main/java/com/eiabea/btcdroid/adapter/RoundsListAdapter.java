package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

import java.text.ParseException;
import java.util.Date;

public class RoundsListAdapter extends CursorTreeAdapter {

    public static final String TAG = RoundsListAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    private Context context;

    public RoundsListAdapter(Cursor cursor, Context context) {
        super(cursor, context, true);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        String selection = Block.NUMBER + "=?";
        String[] selectionArgs = new String[]{groupCursor.getString(groupCursor.getColumnIndex(Block.NUMBER))};

        return context.getContentResolver().query(Block.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        View v = inflater.inflate(R.layout.view_block_header, parent, false);

        RoundHeader rh = new RoundHeader();

        rh.txtDuration = (TextView) v.findViewById(R.id.txt_block_header_duration);
        rh.txtReward = (TextView) v.findViewById(R.id.txt_block_header_reward);

        rh.flBottomSpacerWhite = (FrameLayout) v.findViewById(R.id.fl_block_bottom_spacer_white);
        rh.flBottomSpacerGrey = (FrameLayout) v.findViewById(R.id.fl_block_bottom_spacer_grey);
        rh.flDivider = (FrameLayout) v.findViewById(R.id.fl_block_divider);

        v.setTag(rh);

        return v;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        RoundHeader rh = (RoundHeader) view.getTag();

        Block block = new Block(cursor);

        rh.txtDuration.setText(block.getMining_duration());

        setReward(rh.txtReward, block);

        if (isExpanded) {
            rh.flBottomSpacerWhite.setVisibility(View.VISIBLE);
            rh.flBottomSpacerGrey.setVisibility(View.GONE);
            rh.flDivider.setVisibility(View.VISIBLE);
        } else {
            rh.flBottomSpacerWhite.setVisibility(View.GONE);
            rh.flBottomSpacerGrey.setVisibility(View.VISIBLE);
            rh.flDivider.setVisibility(View.GONE);
        }
    }

    private void setReward(TextView txt, Block currentBlock) {

        if (currentBlock.getReward() != null) {
            float reward = Float.valueOf(currentBlock.getReward());
            int confirmationsLeft = 100 - currentBlock.getConfirmations();

            txt.setText(App.formatReward(reward));

            if (confirmationsLeft <= 0) {
                txt.setTextColor(context.getResources().getColor(R.color.bd_circle_green_solid));
            } else {
                txt.setTextColor(context.getResources().getColor(R.color.bd_orange));
            }
        } else {
            txt.setText(context.getString(R.string.txt_not_available));
            txt.setTextColor(context.getResources().getColor(R.color.bd_circle_red_solid));
        }

    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        View v = inflater.inflate(R.layout.view_block, parent, false);

        RoundBody rb = new RoundBody();

        rb.txtConfirmations = (TextView) v.findViewById(R.id.txt_block_confirmations);
        rb.txtFound = (TextView) v.findViewById(R.id.txt_block_found);
        rb.txtStarted = (TextView) v.findViewById(R.id.txt_block_started);

        v.setTag(rb);

        return v;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        RoundBody rb = (RoundBody) view.getTag();

        Block block = new Block(cursor);

        setConfirmation(rb.txtConfirmations, block);

        Date found = null;
        Date started = null;

        try {
            found = App.dateStatsFormat.parse(block.getDate_found());
            started = App.dateStatsFormat.parse(block.getDate_started());
        } catch (ParseException e) {
            Log.e(getClass().getSimpleName(), "Can't parse found or started Date (ParseExeception)");
        }

        if (found != null) {
            rb.txtFound.setText(App.dateFormat.format(found));
        }
        if (started != null) {
            rb.txtStarted.setText(App.dateFormat.format(started));
        }

    }

    private void setConfirmation(TextView txt, Block currentBlock) {

        int confirmationsLeft = 100 - currentBlock.getConfirmations();

        if (confirmationsLeft <= 0) {
            txt.setText(R.string.txt_confirmed);
            txt.setTextColor(context.getResources().getColor(R.color.bd_circle_green_solid));
        } else {
            txt.setText(String.valueOf(confirmationsLeft));
            txt.setTextColor(context.getResources().getColor(R.color.bd_orange));
        }

    }

    public static class RoundHeader {
        TextView txtDuration, txtReward;

        FrameLayout flBottomSpacerWhite, flBottomSpacerGrey, flDivider;

    }

    public static class RoundBody {
        TextView txtConfirmations, txtFound, txtStarted;
    }

}
