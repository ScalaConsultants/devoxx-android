package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.SpeakerItem.NameComparator;
import io.scalac.degree.items.TalkItem;

import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class SpeakersFragment extends Fragment {
	
	private ItemAdapter		listAdapter;
	ArrayList<TalkItem>		talkItemsList;
	ArrayList<SpeakerItem>	speakerItemsList;
	boolean						isCreated;
	
	public static SpeakersFragment newInstance() {
		SpeakersFragment fragment = new SpeakersFragment();
		return fragment;
	}
	
	public SpeakersFragment() {}
	
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
		setRetainInstance(true);
		talkItemsList = getMainActivity().getTalkItemsList();
		speakerItemsList = new ArrayList<SpeakerItem>(getMainActivity().getSpeakerItemsList());
		Collections.sort(speakerItemsList, new NameComparator());
		listAdapter = new ItemAdapter();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getMainActivity().setDrawerIndicatorEnabled(true);
		
		final View rootView = inflater.inflate(R.layout.fragment_talks, container, false);
		
		final ListView listViewTalks = (ListView) rootView;
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(SpeakerFragment.newInstance(speakerItemsList.get(position).getId()), true);
			}
		});
		return rootView;
	}
	
	class ItemAdapter extends BaseAdapter {
		
		private class ViewHolder {
			public TextView	textSpeaker;
		}
		
		@Override
		public int getCount() {
			return speakerItemsList.size();
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
				viewItem = getActivity().getLayoutInflater().inflate(R.layout.speakers_all_list_item, parent, false);
				holder = new ViewHolder();
				holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeaker);
				viewItem.setTag(holder);
			} else {
				viewItem = convertView;
				holder = (ViewHolder) viewItem.getTag();
			}
			SpeakerItem speakerItem = speakerItemsList.get(position);
			holder.textSpeaker.setText(speakerItem.getName());
			return viewItem;
		}
	}
}
