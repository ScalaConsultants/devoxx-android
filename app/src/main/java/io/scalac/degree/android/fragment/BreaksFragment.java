package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import io.scalac.degree.items.BreakItem;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

/**
 * A placeholder fragment containing a simple view.
 */
@EFragment(R.layout.items_list_view)
public class BreaksFragment extends BaseFragment {

	@FragmentArg int timeslotID;

	private ItemAdapter listAdapter;
	ArrayList<BreakItem> breakItemsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		init();
	}

	private void init() {
		breakItemsList = BreakItem.getTimeslotBreakList(dataSource.getBreakItemsList(), timeslotID);
		listAdapter = new ItemAdapter();
	}

	@AfterViews void afterViews() {
		final ListView listViewBreaks = (ListView) getView();
		listViewBreaks.addFooterView(Utils.getFooterView(getActivity()));
		listViewBreaks.setFooterDividersEnabled(false);
		listViewBreaks.setAdapter(listAdapter);
		listViewBreaks.setOnItemClickListener(null);
		listViewBreaks.setItemsCanFocus(true);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	class ItemAdapter extends BaseAdapter {

		private class ViewHolder {
			public TextView textTitle;
			public TextView textDesc;
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
