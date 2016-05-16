package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.util.App;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RoundsListAdapter extends CursorTreeAdapter {

    public static final String TAG = RoundsListAdapter.class.getSimpleName();

    private final Context context;
    private GenericPrice currentPrice;

    public RoundsListAdapter(Context context) {
        super(null, context, true);
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
        LayoutInflater inflater = LayoutInflater.from(context);

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

        long milliseconds = block.getMining_duration() * 1000;

        long days = TimeUnit.MILLISECONDS
                .toDays(milliseconds);
        milliseconds -= TimeUnit.DAYS.toMillis(days);

        long hours = TimeUnit.MILLISECONDS
                .toHours(milliseconds);
        milliseconds -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS
                .toMinutes(milliseconds);
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS
                .toSeconds(milliseconds);

        String durationString;

        if (days > 1) {
            durationString = String.format(Locale.getDefault(), "%d days - %02d:%02d:%02d", days, hours, minutes, seconds);
        } else if (days == 1) {
            durationString = String.format(Locale.getDefault(), "%d day - %02d:%02d:%02d", days, hours, minutes, seconds);
        } else {
            durationString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }

        rh.txtDuration.setText(durationString);

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

        int style = Integer.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getString("btc_style_preference", "0"));

        if (currentBlock.getReward() != null) {
            float reward = Float.valueOf(currentBlock.getReward());
            int confirmationsLeft = 100 - currentBlock.getConfirmations();

            if (style == 3 && this.currentPrice != null) {
                txt.setText(App.formatPrice(this.currentPrice.getSymbol(), reward * this.currentPrice.getValueFloat()));
            } else {
                txt.setText(App.formatReward(reward));
            }


            if (confirmationsLeft <= 0) {
                txt.setTextColor(ContextCompat.getColor(context, R.color.bd_circle_green_solid));
            } else {
                txt.setTextColor(ContextCompat.getColor(context, R.color.bd_orange));
            }
        } else {
            txt.setText(context.getString(R.string.txt_not_available));
            txt.setTextColor(ContextCompat.getColor(context, R.color.bd_circle_red_solid));
        }

    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

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
            txt.setTextColor(ContextCompat.getColor(context, R.color.bd_circle_green_solid));
        } else {
            txt.setText(String.valueOf(confirmationsLeft));
            txt.setTextColor(ContextCompat.getColor(context, R.color.bd_orange));
        }

    }

    public void setCurrentPrice(GenericPrice currentPrice) {
        this.currentPrice = currentPrice;
        notifyDataSetInvalidated();
    }

    public static class RoundHeader {
        TextView txtDuration, txtReward;

        FrameLayout flBottomSpacerWhite, flBottomSpacerGrey, flDivider;

    }

    public static class RoundBody {
        TextView txtConfirmations, txtFound, txtStarted;
    }

}
