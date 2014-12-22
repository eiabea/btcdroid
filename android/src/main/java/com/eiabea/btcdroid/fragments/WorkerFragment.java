package com.eiabea.btcdroid.fragments;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.WorkerListAdapter;
import com.eiabea.btcdroid.adapter.WorkerListAdapter.Holder;
import com.eiabea.btcdroid.model.Profile;
import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.util.App;

import java.util.ArrayList;
import java.util.Collections;

public class WorkerFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String PARAM_PROFILE = "param_profile";
    private static final int WORKER_PROFILE_LOADER_ID = 333;

    private ExpandableListView exlvWOrkerHolder;

    private WorkerListAdapter adapter;

//    private DisplayMetrics metrics;
//    private int width;

    public static WorkerFragment create() {
        WorkerFragment fragment = new WorkerFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView()");
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_worker, null);

        initUi(inflater, rootView);

        getActivity().getSupportLoaderManager().initLoader(WORKER_PROFILE_LOADER_ID, null, this);

        return rootView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void initUi(LayoutInflater inflater, ViewGroup rootView) {

//        metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        width = metrics.widthPixels;

        exlvWOrkerHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_worker_holder);

//        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
//            exlvWOrkerHolder.setIndicatorBounds(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
//        } else {
//            exlvWOrkerHolder.setIndicatorBoundsRelative(width - App.getDipsFromPixel(78, getActivity()), width - App.getDipsFromPixel(0, getActivity()));
//        }

    }

    private void setProfile(Profile profile) {
        try {
            ArrayList<Worker> list = profile.getWorkersList();

            Collections.sort(list, new App.WorkerSorter());

            adapter = new WorkerListAdapter(getActivity());

            adapter.setData(list);

            exlvWOrkerHolder.setAdapter(adapter);

            expandActiveWorker();

        } catch (NullPointerException ignore) {

        }
    }

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
        String[] selectionArgs = { "1" };

        return new CursorLoader(getActivity(), Profile.CONTENT_URI, null, selection, selectionArgs, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0) {
            switch (loader.getId()) {
                case WORKER_PROFILE_LOADER_ID:
                    c.moveToFirst();

                    Profile profile = new Profile(c);
                    profile = App.getInstance().gson.fromJson(profile.getJson(), Profile.class);

                    if (profile != null) {
                        setProfile(profile);
                    }
                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

}
