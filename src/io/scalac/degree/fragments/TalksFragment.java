package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TalkItem.TopicComparator;
import io.scalac.degree.utils.ItemNotFoundException;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

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
	private static final String	ARG_ROOM_ID			= "room_id";
	private static final String	ARG_TIMESLOT_ID	= "timeslot_id";
	private static final String	ARG_DATE_MS			= "date_ms";
	private ItemAdapter				listAdapter;
	private int							roomID;
	private int							timeslotID;
	ArrayList<TalkItem>				talkItemsList;
	ArrayList<SpeakerItem>			speakerItemsList;
	ArrayList<RoomItem>				roomItemsList;
	DateFormat							timeFormat;
	boolean								is12HourFormat;
	boolean								isCreated;
	TalksType							talksType			= TalksType.ALL;
	int									itemLayoutID;
	
	public enum TalksType {
		ALL, ROOM, TIME
	}
	
	public static TalksFragment newInstance() {
		TalksFragment fragment = new TalksFragment();
		return fragment;
	}
	
	public static TalksFragment newInstanceRoom(int roomID, long dateMS) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_ROOM_ID, roomID);
		args.putLong(ARG_DATE_MS, dateMS);
		fragment.setArguments(args);
		return fragment;
	}
	
	public static TalksFragment newInstanceTime(int timeslotID) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_TIMESLOT_ID, timeslotID);
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
		if (getArguments() != null && getArguments().containsKey(ARG_ROOM_ID)) {
			roomID = getArguments().getInt(ARG_ROOM_ID);
			talksType = TalksType.ROOM;
			talkItemsList = TalkItem.getRoomTalkList(getMainActivity().getTalkItemsList(),
					roomID,
					getArguments().getLong(ARG_DATE_MS));
			itemLayoutID = R.layout.talks_room_list_item;
		} else if (getArguments() != null && getArguments().containsKey(ARG_TIMESLOT_ID)) {
			timeslotID = getArguments().getInt(ARG_TIMESLOT_ID);
			talkItemsList = TalkItem.getTimeslotTalkList(getMainActivity().getTalkItemsList(), timeslotID);
			roomItemsList = getMainActivity().getRoomItemsList();
			talksType = TalksType.TIME;
			itemLayoutID = R.layout.talks_time_list_item;
		} else {
			setRetainInstance(true);
			talksType = TalksType.ALL;
			talkItemsList = new ArrayList<TalkItem>(getMainActivity().getTalkItemsList());
			Collections.sort(talkItemsList, new TopicComparator());
			itemLayoutID = R.layout.talks_all_list_item;
		}
		speakerItemsList = getMainActivity().getSpeakerItemsList();
		timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
		is12HourFormat = !android.text.format.DateFormat.is24HourFormat(getActivity());
		listAdapter = new ItemAdapter();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (talksType == TalksType.ALL)
			getMainActivity().setDrawerIndicatorEnabled(true);
		
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
			public TextView		textTopic;
			public TextView		textRoom;
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
				viewItem = getActivity().getLayoutInflater().inflate(itemLayoutID, parent, false);
				holder = new ViewHolder();
				holder.textTopic = (TextView) viewItem.findViewById(R.id.textTopic);
				holder.textSpeaker = (TextView) viewItem.findViewById(R.id.textSpeakers);
				if (talksType == TalksType.ROOM) {
					holder.textTimeStart = (TextView) viewItem.findViewById(R.id.textTimeStart);
					holder.textTimeEnd = (TextView) viewItem.findViewById(R.id.textTimeEnd);
					if (is12HourFormat) {
						LinearLayout linearLayoutTime = (LinearLayout) viewItem.findViewById(R.id.linearLayoutTime);
						linearLayoutTime.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.width_12h);
					}
				} else if (talksType == TalksType.TIME) {
					holder.textRoom = (TextView) viewItem.findViewById(R.id.textRoom);
				}
				holder.imageButtonNotify = (ImageButton) viewItem.findViewById(R.id.imageButtonNotify);
				holder.imageButtonNotify.setOnClickListener(alarmOnClick);
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
			holder.textTopic.setText(talkItem.getTopicHtml());
			holder.textSpeaker.setText(speakers);
			if (talksType == TalksType.ROOM) {
				holder.textTimeStart.setText(timeFormat.format(talkItem.getStartTime()));
				holder.textTimeEnd.setText(timeFormat.format(talkItem.getEndTime()));
			} else if (talksType == TalksType.TIME) {
				try {
					holder.textRoom.setText(RoomItem.getByID(talkItem.getRoomID(), roomItemsList).getName());
				} catch (ItemNotFoundException e) {
					// e.printStackTrace();
					holder.textRoom.setText("");
				}
			}
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
