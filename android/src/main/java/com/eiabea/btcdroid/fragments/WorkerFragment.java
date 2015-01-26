package com.eiabea.btcdroid.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.WorkerListAdapter;
import com.eiabea.btcdroid.model.Worker;

public class WorkerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int WORKER_LOADER_ID = 333;

    private ViewGroup rootView;

    private ExpandableListView exlvWOrkerHolder;

    private WorkerListAdapter adapter;

    public static WorkerFragment create() {
        WorkerFragment fragment = new WorkerFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(WORKER_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_worker, null);

        initUi();

        adapter = new WorkerListAdapter(null, getActivity());

        exlvWOrkerHolder.setAdapter(adapter);

        return rootView;
    }

    private void initUi() {
        exlvWOrkerHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_worker_holder);
    }

//    private void expandActiveWorker() {
//        for (int i = 0; i < adapter.getGroupCount(); i++) {
//
//            Cursor c = adapter.getGroup(i);
//
//            boolean isAlive = c.getInt(c.getColumnIndex(Worker.ALIVE)) == 1;
//
//            if (isAlive) {
//                exlvWOrkerHolder.expandGroup(i);
//            }
//
//        }
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {
        String sort = Worker.WORKER_NAME + " DESC";

        return new CursorLoader(getActivity(), Worker.CONTENT_URI, null, null, null, sort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {
            switch (loader.getId()) {
                case WORKER_LOADER_ID:
                    c.moveToFirst();

                    adapter.setGroupCursor(c);

//                    expandActiveWorker();
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

}
