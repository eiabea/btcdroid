package com.eiabea.btcdroid.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.eiabea.btcdroid.model.Worker;
import com.eiabea.btcdroid.views.WorkerView;
import com.eiabea.btcdroid.views.WorkerViewHeader;

public class WorkerListAdapter extends BaseExpandableListAdapter {

	private Context context;

	private List<Holder> holders = new ArrayList<WorkerListAdapter.Holder>();

	public WorkerListAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<Worker> workers) {
		
		for(Worker tmpWorker : workers){
			holders.add(new Holder(tmpWorker.getName(), tmpWorker));
		}
		
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return holders.get(groupPosition).getWorker();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		WorkerView view = (WorkerView) convertView;

		if (view == null) {
			view = new WorkerView(context);
		}

		view.setData(holders.get(groupPosition).getWorker());

		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {

		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return holders.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		if (holders != null) {
			return holders.size();
		}
		return 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		WorkerViewHeader view = (WorkerViewHeader) convertView;

		if (view == null) {
			view = new WorkerViewHeader(context);
		}

		view.setData(holders.get(groupPosition).getWorker(), isExpanded);
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
	
	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		// TODO Auto-generated method stub
		super.onGroupExpanded(groupPosition);
	}



	public class Holder{
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
