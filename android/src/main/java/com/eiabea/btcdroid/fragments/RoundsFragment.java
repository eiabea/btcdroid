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
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.util.App;

public class RoundsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ROUNDS_ROUNDS_LOADER_ID = 411;
    private static final int ROUNDS_PRICE_LOADER_ID = 412;

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

        getActivity().getSupportLoaderManager().initLoader(ROUNDS_ROUNDS_LOADER_ID, null, this);
        getActivity().getSupportLoaderManager().initLoader(ROUNDS_PRICE_LOADER_ID, null, this);
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

        String selection;
        String[] selectionArgs = {"1"};
        switch (which) {
            case ROUNDS_ROUNDS_LOADER_ID:
                String sort = Block.NUMBER + " DESC";

                return new CursorLoader(getActivity(), Block.CONTENT_URI, null, null, null, sort);
            case ROUNDS_PRICE_LOADER_ID:
                selection = GenericPrice._ID + "=?";
                return new CursorLoader(getActivity(), GenericPrice.CONTENT_URI, null, selection, selectionArgs, null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {

        if (c.getCount() > 0 && isAdded()) {
            c.moveToFirst();

            switch (loader.getId()) {
                case ROUNDS_ROUNDS_LOADER_ID:

                    adapter.setGroupCursor(c);

                    break;
                case ROUNDS_PRICE_LOADER_ID:

                    GenericPrice price = new GenericPrice(c);
                    price = App.getInstance().gson.fromJson(price.getJson(), GenericPrice.class);

                    adapter.setCurrentPrice(price);

                    break;
            }


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }
}
