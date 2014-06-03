package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.R;
import io.scalac.degree.Utils;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;

import java.text.DateFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class TalksFragment extends Fragment {
	/**
	 * The fragment argument representing the section number for this fragment.
	 */
	private static final String	ARG_ROOM_ID	= "room_id";
	private ItemAdapter				listAdapter;
	private int							roomID;
	ArrayList<TalkItem>				talkItemsList;
	ArrayList<SpeakerItem>			speakerItemsList;
	DateFormat							timeFormat;
	boolean								is12HourFormat;
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static TalksFragment newInstance(int roomID) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_ROOM_ID, roomID);
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
		roomID = getArguments().getInt(ARG_ROOM_ID);
		talkItemsList = TalkItem.getRoomTalkList(getMainActivity().getTalkItemsList(), roomID);
		speakerItemsList = getMainActivity().getSpeakerItemsList();
		timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
		is12HourFormat = !android.text.format.DateFormat.is24HourFormat(getActivity());
		listAdapter = new ItemAdapter();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		listAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_talks, container, false);
		
		final ListView listViewTalks = (ListView) rootView;
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(TalkFragment.newInstance(talkItemsList.get(position).getId()), true);
			}
		});
		return rootView;
	}
	
	class ItemAdapter extends BaseAdapter {
		
		private class ViewHolder {
			public TextView		textTitle;
			public TextView		textSpeaker;
			public TextView		textTimeStart;
			public TextView		textTimeEnd;
			public ImageButton	imageButtonNotify;
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
				holder.imageButtonNotify = (ImageButton) viewItem.findViewById(R.id.imageButtonNotify);
				holder.imageButtonNotify.setOnClickListener(alarmOnClick);
				if (is12HourFormat) {
					LinearLayout linearLayoutTime = (LinearLayout) viewItem.findViewById(R.id.linearLayoutTime);
					linearLayoutTime.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.width_12h);
				}
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
			boolean isAlarmSet = getMainActivity().getNotifyMap().containsKey(String.valueOf(talkItem.getId()));
			holder.imageButtonNotify.setImageResource(isAlarmSet ? R.drawable.ic_action_device_access_alarms
					: R.drawable.ic_action_alerts_and_states_add_alarm);
			holder.imageButtonNotify.setTag(position);
			return viewItem;
		}
		
		private final AlarmOnClick	alarmOnClick	= new AlarmOnClick();
		
		private class AlarmOnClick implements OnClickListener {
			
			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				TalkItem talkItem = talkItemsList.get(position);
				boolean isAlarmSet = getMainActivity().getNotifyMap().containsKey(String.valueOf(talkItem.getId()));
				if (isAlarmSet)
					Utils.unsetNotify(getActivity().getApplicationContext(), talkItem.getId());
				else
					Utils.setNotify(getActivity().getApplicationContext(), talkItem.getId(), talkItem.getStartTime()
							.getTime(), true);
				getMainActivity().setNotifyMap(Utils.getAlarms(getActivity().getApplicationContext()));
				listAdapter.notifyDataSetChanged();
			}
		}
	}
}
