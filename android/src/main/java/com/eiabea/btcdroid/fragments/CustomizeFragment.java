package com.eiabea.btcdroid.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.eiabea.btcdroid.MainActivity;
import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.CustomizeAdapter;
import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.model.CustomizeItem;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;
import java.util.List;

public class CustomizeFragment extends Fragment {

    private ArrayAdapter<CustomizeItem> adapter;

    private Spinner spnMainFragment;

    private DragSortListView mDslv;

    private SharedPreferences pref;

    public int dragStartMode = DragSortController.ON_DOWN;
    public boolean removeEnabled = true;
    public int removeMode = DragSortController.FLING_REMOVE;
    public boolean sortEnabled = true;
    public boolean dragEnabled = true;

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
        @Override
        public void drop(int from, int to) {
            if (from != to) {
                CustomizeItem item = adapter.getItem(from);
                adapter.remove(item);
                adapter.insert(item, to);
            }
            updateOrderList();
        }
    };

    private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
        @Override
        public void remove(int which) {
            if (adapter.getCount() > 1) {
                adapter.remove(adapter.getItem(which));
            } else {
                adapter.notifyDataSetChanged();
            }
            updateOrderList();
        }

    };

    private void updateOrderList() {
        ArrayList<String> userList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < adapter.getCount(); i++) {
            builder.append(adapter.getItem(i).getId());
            if (i < adapter.getCount() - 1) {
                builder.append(":");
            }
            Log.d(getClass().getSimpleName(), "count: " + i + " = " + adapter.getItem(i).getId());
            userList.add(String.valueOf(adapter.getItem(i).getId()));
        }
        Log.d(getClass().getSimpleName(), "userOrder: " + builder.toString());

        pref.edit().putString("userOrder", builder.toString()).apply();

        // Set Spinner
        List<String> spinnerList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            // +1 to use userfriendly indexes
            spinnerList.add(String.valueOf(i + 1));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMainFragment.setAdapter(dataAdapter);
        int selection = pref.getInt("userMainFragment", MainActivity.FRAGMENT_POOL);
        if (selection < spnMainFragment.getAdapter().getCount()) {
            spnMainFragment.setSelection(selection);
        } else {
            spnMainFragment.setSelection(0);
        }
    }

    protected int getLayout() {
        // this DSLV xml declaration does not call for the use
        // of the default DragSortController; therefore,
        // DSLVFragment has a buildController() method.
        return R.layout.fragment_customize;
    }

    /**
     * Called from DSLVFragment.onActivityCreated(). Override to set a different
     * adapter.
     */
    public void setListAdapter() {

        String userOrder = pref.getString("userOrder", "0:1:2:3");
        String[] split = userOrder.split(":");

        ArrayList<CustomizeItem> list = new ArrayList<>(split.length);
        for (String aSplit : split) {
            list.add(new CustomizeItem(Integer.valueOf(aSplit), MainViewAdapter.getNameOfFragment(Integer.valueOf(aSplit), getActivity())));
        }

        adapter = new CustomizeAdapter(getActivity(), R.layout.list_item_handle_left, list);
        mDslv.setAdapter(adapter);
        updateOrderList();
    }

    /**
     * Called in onCreateView. Override this to provide a custom
     * DragSortController.
     */
    public DragSortController buildController(DragSortListView dslv) {
        // defaults are
        // dragStartMode = onDown
        // removeMode = flingRight
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setRemoveEnabled(removeEnabled);
        controller.setSortEnabled(sortEnabled);
        controller.setDragInitMode(dragStartMode);
        controller.setRemoveMode(removeMode);
        return controller;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());


        ViewGroup rootView = (ViewGroup) inflater.inflate(getLayout(), container, false);

        spnMainFragment = (Spinner) rootView.findViewById(R.id.spn_customize_main_fragment);

        spnMainFragment.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                pref.edit().putInt("userMainFragment", position).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        mDslv = (DragSortListView) rootView.findViewById(android.R.id.list);

        mDslv.setDropListener(onDrop);
        mDslv.setRemoveListener(onRemove);

        DragSortController mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(dragEnabled);

        setListAdapter();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.customize, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_accept:
                getActivity().finish();
                break;
            case R.id.action_reset:
                showResetAlert();

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResetAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warining");
        builder.setMessage("Would you like to reset to the default settings?");
        builder.setPositiveButton("Yes", new OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // Force default order
                pref.edit().remove("userMainFragment").apply();
                pref.edit().remove("userOrder").apply();
                setListAdapter();
            }
        });
        builder.setNegativeButton("Cancel", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
            }
        });
        builder.create().show();
    }
}