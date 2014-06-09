package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.items.BreakItem;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class BreaksFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String	ARG_TIMESLOT_ID	= "timeslot_id";
	private ItemAdapter				listAdapter;
	private int							timeslotID;
	ArrayList<BreakItem>				breakItemsList;
	boolean								isCreated;
	
	public static BreaksFragment newInstanceTime(int timeslotID) {
		BreaksFragment fragment = new BreaksFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TIMESLOT_ID, timeslotID);
		fragment.setArguments(args);
		return fragment;
	}
	
	public BreaksFragment() {}
	
	private MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getActivity() != null) {
			init();
			isCreated = true;
		} else
			isCreated = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		listAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (!isCreated) {
			init();
			isCreated = true;
		}
	}
	
	private void init() {
		timeslotID = getArguments().getInt(ARG_TIMESLOT_ID);
		breakItemsList = BreakItem.getTimeslotBreakList(getMainActivity().getBreakItemsList(), timeslotID);
		listAdapter = new ItemAdapter();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		final View rootView = inflater.inflate(R.layout.items_list_view, container, false);
		
		final ListView listViewBreaks = (ListView) rootView;
		listViewBreaks.setAdapter(listAdapter);
		listViewBreaks.addFooterView(Utils.getFooterView(getActivity()));
		listViewBreaks.setFooterDividersEnabled(false);
		listViewBreaks.setOnItemClickListener(null);
		listViewBreaks.setItemsCanFocus(true);
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	class ItemAdapter extends BaseAdapter {
		
		private class ViewHolder {
			public TextView	textTitle;
			public TextView	textDesc;
		}
		
		@Override
		public int getCount() {
			return breakItemsList.size();
		}
		
		@Override
		public Object getItem(int position) {
			return position;
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			
			View viewItem;
			ViewHolder holder;
			
			if (convertView == null) {
				viewItem = getActivity().getLayoutInflater().inflate(R.layout.break_list_item, parent, false);
				holder = new ViewHolder();
				holder.textTitle = (TextView) viewItem.findViewById(R.id.textTitle);
				holder.textDesc = (TextView) viewItem.findViewById(R.id.textDesc);
				viewItem.setTag(holder);
			} else {
				viewItem = convertView;
				holder = (ViewHolder) viewItem.getTag();
			}
			
			BreakItem breakItem = breakItemsList.get(position);
			if (breakItem.hasTitle()) {
				holder.textTitle.setVisibility(View.VISIBLE);
				holder.textTitle.setText(breakItem.getTitleHtml());
			} else
				holder.textTitle.setVisibility(View.GONE);
			if (breakItem.hasDescription()) {
				holder.textDesc.setVisibility(View.VISIBLE);
				holder.textDesc.setText(breakItem.getDescriptionHtml());
			} else
				holder.textDesc.setVisibility(View.GONE);
			
			return viewItem;
		}
	}
}
