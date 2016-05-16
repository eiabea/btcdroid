package com.eiabea.btcdroid.fragments;

import android.database.Cursor;
import android.os.Bundle;
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
import com.eiabea.btcdroid.adapter.RoundsListAdapter;
import com.eiabea.btcdroid.model.Block;
import com.eiabea.btcdroid.model.GenericPrice;
import com.eiabea.btcdroid.util.App;

public class RoundsFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        LoaderManager.LoaderCallbacks<Cursor>,
        AbsListView.OnScrollListener {

    private static final int ROUNDS_ROUNDS_LOADER_ID = 411;
    private static final int ROUNDS_PRICE_LOADER_ID = 412;

    private RoundsListAdapter adapter;

    private SwipeRefreshLayout swipeLayout;

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_rounds, container, false);

        initUi(rootView);

        setListeners();

        adapter = new RoundsListAdapter(getActivity());

        exlvRoundsHolder.setAdapter(adapter);

        return rootView;
    }

    private void initUi(ViewGroup rootView) {
        exlvRoundsHolder = (ExpandableListView) rootView.findViewById(R.id.exlv_main_rounds_holder);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setColorSchemeResources(R.color.bd_actionbar_background, R.color.bd_black);
    }

    private void setListeners() {
        swipeLayout.setOnRefreshListener(this);
        exlvRoundsHolder.setOnScrollListener(this);
    }

    private void handleLoading() {
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onRefresh() {
        if (App.getInstance().isTokenSet()) {
            App.updateData(getActivity());
        }
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

            handleLoading();


        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (exlvRoundsHolder == null || exlvRoundsHolder.getChildCount() == 0) ?
                        0 : exlvRoundsHolder.getChildAt(0).getTop();
        swipeLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }
}
