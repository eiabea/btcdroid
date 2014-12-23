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
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

import java.util.Date;

public class WorkerListAdapter extends CursorTreeAdapter {

    public static final String TAG = WorkerListAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    private Context context;

    public WorkerListAdapter(Cursor cursor, Context context) {
        super(cursor, context, true);
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {

        String selection = Worker.NAME + "=?";
        String[] selectionArgs = new String[]{groupCursor.getString(groupCursor.getColumnIndex(Worker.NAME))};

        return context.getContentResolver().query(Worker.CONTENT_URI, null, selection, selectionArgs, null);
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {

        View v = inflater.inflate(R.layout.view_worker_header, parent, false);

        WorkerHeader wh = new WorkerHeader();

        wh.imgCircle = (ImageView) v.findViewById(R.id.img_worker_header_cirlce);

        wh.txtName = (TextView) v.findViewById(R.id.txt_worker_name);
        wh.txtStatus = (TextView) v.findViewById(R.id.txt_worker_status);

        wh.flBottomSpacerWhite = (FrameLayout) v.findViewById(R.id.fl_worker_bottom_spacer_white);
        wh.flBottomSpacerGrey = (FrameLayout) v.findViewById(R.id.fl_worker_bottom_spacer_grey);
        wh.flDivider = (FrameLayout) v.findViewById(R.id.fl_worker_divider);

        v.setTag(wh);

        return v;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {

        WorkerHeader wh = (WorkerHeader) view.getTag();

        Worker worker = new Worker(cursor);

        Typeface tfRegular = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Regular.ttf");
        Typeface tfItalic = Typeface.createFromAsset(context.getAssets(), "RobotoCondensed-Italic.ttf");

        boolean showUsername = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("username_workers", true);

        if (showUsername) {
            wh.txtName.setText(worker.getName());
        } else {

            try {

                String fullString = worker.getName();
                String workerName = "";
                int dotIndex = fullString.indexOf(".");
                if (dotIndex > -1) {
                    // Increase index to cut out the dot
                    dotIndex++;
                    workerName = fullString.substring(dotIndex);
                    wh.txtName.setText(workerName);
                } else {
                    throw new NullPointerException("no Dot");
                }
            } catch (NullPointerException e) {

                wh.txtName.setText(worker.getName());
            }
        }

        if (worker.isAlive()) {
            wh.imgCircle.setImageResource(R.drawable.shape_circle_green);
            wh.txtStatus.setText(App.formatHashRate(worker.getHashrate()));
            wh.txtStatus.setTypeface(tfRegular);
            wh.txtStatus.setTextColor(context.getResources().getColor(R.color.bd_circle_green_solid));
        } else {
            wh.imgCircle.setImageResource(R.drawable.shape_circle_red);
            wh.txtStatus.setText(context.getString(R.string.txt_inactive));
            wh.txtStatus.setTypeface(tfItalic);
            wh.txtStatus.setTextColor(context.getResources().getColor(R.color.bd_circle_red_solid));
        }

        if (isExpanded) {
            wh.flBottomSpacerWhite.setVisibility(View.VISIBLE);
            wh.flBottomSpacerGrey.setVisibility(View.GONE);
            wh.flDivider.setVisibility(View.VISIBLE);
        } else {
            wh.flBottomSpacerWhite.setVisibility(View.GONE);
            wh.flBottomSpacerGrey.setVisibility(View.VISIBLE);
            wh.flDivider.setVisibility(View.GONE);
        }
    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        View v = inflater.inflate(R.layout.view_worker, parent, false);

        WorkerBody wb = new WorkerBody();

        wb.txtScore = (TextView) v.findViewById(R.id.txt_worker_score);
        wb.txtLastShare = (TextView) v.findViewById(R.id.txt_worker_last_share);
        wb.txtHashrate = (TextView) v.findViewById(R.id.txt_worker_hashrate);
        wb.txtShares = (TextView) v.findViewById(R.id.txt_worker_shares);

        v.setTag(wb);

        return v;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        WorkerBody wb = (WorkerBody) view.getTag();

        Worker worker = new Worker(cursor);

        wb.txtScore.setText(worker.getScore());
        wb.txtLastShare.setText(App.dateFormat.format(new Date(worker.getLast_share() * 1000)));
        wb.txtHashrate.setText(App.formatHashRate(worker.getHashrate()));
        wb.txtShares.setText(String.valueOf(worker.getShares()));
    }

    public static class WorkerHeader {
        TextView txtName, txtStatus;
        ImageView imgCircle;
        FrameLayout flBottomSpacerWhite, flBottomSpacerGrey, flDivider;

    }

    public static class WorkerBody {
        TextView txtScore, txtLastShare, txtHashrate, txtShares;
    }

}
