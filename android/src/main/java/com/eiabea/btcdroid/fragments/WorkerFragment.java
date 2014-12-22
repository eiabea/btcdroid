package com.eiabea.btcdroid.fragments;

import android.database.Cursor;
import android.database.DatabaseUtils;
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
import com.eiabea.btcdroid.adapter.WorkerListAdapter_old.Holder;
import com.eiabea.btcdroid.model.Profile;
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

        return rootView;
    }

    private void initUi() {
        exlvWOrkerHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_worker_holder);
    }

//    private void setProfile(Profile profile) {
//        Log.i(getClass().getSimpleName(), "setProfile()");
//        try {
//            ArrayList<Worker> list = profile.getWorkersList();
//
//            Collections.sort(list, new App.WorkerSorter());
//
//            adapter = new WorkerListAdapter_old(getActivity());
//
//            adapter.setData(list);
//
//            exlvWOrkerHolder.setAdapter(adapter);
//
//            expandActiveWorker();
//
//        } catch (NullPointerException ignore) {
//
//        }
//        Log.i(getClass().getSimpleName(), "setProfile() /done");
//    }

    private void expandActiveWorker() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            Holder holder = (Holder) adapter.getGroup(i);
            if (holder.getWorker().isAlive()) {
                exlvWOrkerHolder.expandGroup(i);
            }

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {


        String selection = Profile._ID + "=?";
        String[] selectionArgs = {"1"};
        String sort = Worker.ALIVE + " DESC, " + Worker.NAME ;

        return new CursorLoader(getActivity(), Worker.CONTENT_URI, null, null, null, sort);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {
            switch (loader.getId()) {
                case WORKER_LOADER_ID:
                    c.moveToFirst();

                    adapter = new WorkerListAdapter(c, getActivity());

                    exlvWOrkerHolder.setAdapter(adapter);

//                    Profile profile = new Profile(c);
////                    profile = App.getInstance().gson.fromJson(profile.getJson(), Profile.class);
//
//                    if (profile != null) {
////                        setProfile(profile);
//
//                    }
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

}
