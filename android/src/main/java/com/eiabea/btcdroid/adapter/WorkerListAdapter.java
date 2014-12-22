package com.eiabea.btcdroid.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.CursorTreeAdapter;

import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.views.WorkerView;
import com.eiabea.btcdroid.views.WorkerViewHeader;

/**
 * Created by eiabea on 12/22/14.
 */
public class WorkerListAdapter extends CursorTreeAdapter {

    public static final String TAG = WorkerListAdapter.class.getSimpleName();

    private LayoutInflater inflater;

    public WorkerListAdapter(Cursor cursor, Context context) {
        super(cursor, context, true);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        return null;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        WorkerViewHeader view = null;

        view = new WorkerViewHeader(context);

        Worker worker = new Worker(cursor);

        view.setData(worker, isExpanded);

        return view;
    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {

    }

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        WorkerView view = new WorkerView(context);

        view.setTag(view);

        return view;
    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        WorkerView viewHolder = (WorkerView) view.getTag();

        Worker worker = new Worker(cursor);

        viewHolder.setData(worker);

    }

    public class Holder {
        private String title;
        private Worker worker;

        public Holder(String title, Worker worker) {
            this.title = title;
            this.worker = worker;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Worker getWorker() {
            return worker;
        }

        public void setWorker(Worker worker) {
            this.worker = worker;
        }
    }
}
