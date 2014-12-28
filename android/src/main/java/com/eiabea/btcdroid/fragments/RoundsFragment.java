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
import com.eiabea.btcdroid.adapter.RoundsListAdapter;
import com.eiabea.btcdroid.model.Block;

public class RoundsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ROUNDS_LOADER_ID = 444;
    private ViewGroup rootView;

    private RoundsListAdapter adapter;

    private ExpandableListView exlvRoundsHolder;

    public static RoundsFragment create() {
        RoundsFragment fragment = new RoundsFragment();
        Bundle b = new Bundle();
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().getSupportLoaderManager().initLoader(ROUNDS_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_rounds, null);

        initUi();

        adapter = new RoundsListAdapter(null, getActivity());

        exlvRoundsHolder.setAdapter(adapter);

        return rootView;
    }

    private void initUi() {
        exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int which, Bundle arg1) {

        String sort = Block.NUMBER + " DESC";

        return new CursorLoader(getActivity(), Block.CONTENT_URI, null, null, null, sort);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        if (c.getCount() > 0 && isAdded()) {

            switch (loader.getId()) {
                case ROUNDS_LOADER_ID:
                    c.moveToFirst();

                    adapter.setGroupCursor(c);

                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
