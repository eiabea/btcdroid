package com.eiabea.btcdroid.fragments;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.eiabea.btcdroid.R;
import com.eiabea.btcdroid.adapter.CustomizeAdapter;
import com.eiabea.btcdroid.adapter.MainViewAdapter;
import com.eiabea.btcdroid.model.CustomizeItem;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class CustomizeFragment extends ListFragment {

	private ArrayAdapter<CustomizeItem> adapter;

//	private int[] array;
	private ArrayList<CustomizeItem> list;
	private ArrayList<String> userList;

	private DragSortListView mDslv;
	private DragSortController mController;
	
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
	
	private void updateOrderList(){
		userList = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		
		for(int i = 0; i < adapter.getCount(); i++){
			builder.append(adapter.getItem(i).getId());
			if(i < adapter.getCount() - 1){
				builder.append(":");
			}
			Log.d(getClass().getSimpleName(), "count: " + i + " = " + adapter.getItem(i).getId());
			userList.add(String.valueOf(adapter.getItem(i).getId()));
		}
		Log.d(getClass().getSimpleName(), "userOrder: " + builder.toString());
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		pref.edit().putString("userOrder", builder.toString()).commit();
	}

	protected int getLayout() {
		// this DSLV xml declaration does not call for the use
		// of the default DragSortController; therefore,
		// DSLVFragment has a buildController() method.
		return R.layout.fragment_customize;
	}

	public static CustomizeFragment newInstance(int headers, int footers) {
		CustomizeFragment f = new CustomizeFragment();

		Bundle args = new Bundle();
		f.setArguments(args);

		return f;
	}

	public DragSortController getController() {
		return mController;
	}

	/**
	 * Called from DSLVFragment.onActivityCreated(). Override to set a different
	 * adapter.
	 */
	public void setListAdapter() {
		
		String userOrder = pref.getString("userOrder", "0:1:2:3");
		String[] split = userOrder.split(":");
		
		list = new ArrayList<CustomizeItem>(split.length);
		for (int i = 0; i < split.length; i++)  {
			list.add(new CustomizeItem(Integer.valueOf(split[i]), MainViewAdapter.getNameOfFragment(Integer.valueOf(split[i]), getActivity()), false));
		}
		
		adapter = new CustomizeAdapter(getActivity(),R.layout.list_item_handle_left, list);
		setListAdapter(adapter);
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

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		mDslv = (DragSortListView) inflater.inflate(getLayout(), container, false);

		mController = buildController(mDslv);
		mDslv.setFloatViewManager(mController);
		mDslv.setOnTouchListener(mController);
		mDslv.setDragEnabled(dragEnabled);

		return mDslv;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.customize, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_reset:
			// Force default order
			pref.edit().remove("userOrder").commit();
			setListAdapter();

			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mDslv = (DragSortListView) getListView();

		mDslv.setDropListener(onDrop);
		mDslv.setRemoveListener(onRemove);

		setListAdapter();
	}
}