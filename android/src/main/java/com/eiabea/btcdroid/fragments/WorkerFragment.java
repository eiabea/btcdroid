package com.eiabea.btcdroid.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ExpandableListView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.WorkerListAdapter;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

public class WorkerFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor>, AbsListView.OnScrollListener {

    private static final int WORKER_LOADER_ID = 333;

    private ExpandableListView exlvWOrkerHolder;

    private WorkerListAdapter adapter;

    private SwipeRefreshLayout swipeLayout;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_worker, container, false);

        initUi(rootView);

        setListeners();

        adapter = new WorkerListAdapter(getActivity());

        exlvWOrkerHolder.setAdapter(adapter);

        return rootView;
    }

    private void initUi(ViewGroup rootView) {
        exlvWOrkerHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_worker_holder);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.bd_actionbar_background, R.color.bd_black);
    }

    private void setListeners() {
        swipeLayout.setOnRefreshListener(this);
        exlvWOrkerHolder.setOnScrollListener(this);
    }

    private void expandActiveWorker() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {

            Cursor c = adapter.getGroup(i);

            boolean isAlive = c.getInt(c.getColumnIndex(Worker.ALIVE)) == 1;
            boolean expand = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("expand_active_workers", true);

            if (isAlive && expand) {
                exlvWOrkerHolder.expandGroup(i);
            }

        }
    }

    private void handleLoading() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {
        String sort = Worker.ALIVE + " DESC, " + Worker.NAME;

        return new CursorLoader(getActivity(), Worker.CONTENT_URI, null, null, null, sort);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0 && isAdded()) {
            switch (loader.getId()) {
                case WORKER_LOADER_ID:
                    c.moveToFirst();

                    adapter.setGroupCursor(c);

                    expandActiveWorker();

                    handleLoading();
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    @Override
    public void onRefresh() {
        if (App.getInstance().isTokenSet()) {
            App.updateData(getActivity());
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (exlvWOrkerHolder == null || exlvWOrkerHolder.getChildCount() == 0) ?
                        0 : exlvWOrkerHolder.getChildAt(0).getTop();
        swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }
}
