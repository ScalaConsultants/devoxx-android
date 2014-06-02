package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;

import java.text.DateFormat;
import java.util.ArrayList;

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
public class TalksFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String	ARG_SECTION_NUMBER	= "section_number";
	private ItemAdapter				listAdapter;
	protected boolean					isCreated				= false;
	private int							sectionNumber;
	ArrayList<TalkItem>				talkItemsList;
	ArrayList<SpeakerItem>			speakerItemsList;
	DateFormat							timeFormat;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static TalksFragment newInstance(int sectionNumber) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_SECTION_NUMBER, sectionNumber);
		fragment.setArguments(args);
		return fragment;
	}
	
	public TalksFragment() {}
	
	private MainActivity getMainActivity() {
		return (MainActivity) getActivity();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
		talkItemsList = TalkItem.getRoomTalkList(getMainActivity().getTalkItemsList(), sectionNumber);
		speakerItemsList = getMainActivity().getSpeakerItemsList();
		timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (!isCreated) {
			listAdapter = new ItemAdapter();
		}
		
		final ListView listViewTalks = (ListView) getView().findViewById(R.id.listView1);
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(TalkFragment.newInstance(talkItemsList.get(position).getId()), true);
			}
		});
		
		isCreated = true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_talks, container, false);
		return rootView;
	}
	
	class ItemAdapter extends BaseAdapter {
		
		private class ViewHolder {
			public TextView	textTitle;
			public TextView	textSpeaker;
			public TextView	textTimeStart;
			public TextView	textTimeEnd;
		}
		
		@Override
		public int getCount() {
			return talkItemsList.size();
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
				viewItem = getActivity().getLayoutInflater().inflate(R.layout.talk_list_item, parent, false);
				holder = new ViewHolder();
				holder.textTitle = (TextView) viewItem.findViewById(R.id.textTitle);
				holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeakers);
				holder.textTimeStart = (TextView) viewItem.findViewById(R.id.textTimeStart);
				holder.textTimeEnd = (TextView) viewItem.findViewById(R.id.textTimeEnd);
				viewItem.setTag(holder);
			} else {
				viewItem = convertView;
				holder = (ViewHolder) viewItem.getTag();
			}
			TalkItem talkItem = talkItemsList.get(position);
			SpeakerItem speakerItem = SpeakerItem.getByID(talkItem.getSpeakerID(), speakerItemsList);
			String speakers = (speakerItem != null) ? speakerItem.getName() : "";
			if (talkItem.hasSpeaker2()) {
				SpeakerItem speaker2Item = SpeakerItem.getByID(talkItem.getSpeaker2ID(), speakerItemsList);
				if (speaker2Item != null)
					speakers += " " + getString(R.string.and) + " " + speaker2Item.getName();
			}
			holder.textTitle.setText(talkItem.getTopic());
			holder.textSpeaker.setText(speakers);
			holder.textTimeStart.setText(timeFormat.format(talkItem.getStartTime()));
			holder.textTimeEnd.setText(timeFormat.format(talkItem.getEndTime()));
			return viewItem;
		}
	}
}
