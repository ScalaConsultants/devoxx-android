package io.scalac.degree.fragments;

import io.scalac.degree.MainActivity;
import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TalkItem.TimeComparator;
import io.scalac.degree.items.TalkItem.TopicComparator;
import io.scalac.degree.utils.ItemNotFoundException;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
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

import com.flurry.android.FlurryAgent;

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
	private static final String	ARG_TAB_TYPE		= "tab_type";
	private ItemAdapter				listAdapter;
	private int							roomID;
	private int							timeslotID;
	ArrayList<TalkItem>				talkItemsList;
	ArrayList<SpeakerItem>			speakerItemsList;
	ArrayList<RoomItem>				roomItemsList;
	DateFormat							timeFormat;
	DateFormat							mediumDateFormat;
	boolean								is12HourFormat;
	boolean								isCreated;
	TalksType							talksType			= TalksType.ALL;
	int									itemLayoutID;
	
	public enum TalksType {
		ALL, ROOM, TIME, NOTIFICATION
	}
	
	public static TalksFragment newInstance() {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TAB_TYPE, TalksType.ALL.name());
		fragment.setArguments(args);
		return fragment;
	}
	
	public static TalksFragment newInstance(TalksType talksType) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TAB_TYPE, talksType.name());
		fragment.setArguments(args);
		return fragment;
	}
	
	public static TalksFragment newInstanceRoom(int roomID, long dateMS) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TAB_TYPE, TalksType.ROOM.name());
		args.putInt(ARG_ROOM_ID, roomID);
		args.putLong(ARG_DATE_MS, dateMS);
		fragment.setArguments(args);
		return fragment;
	}
	
	public static TalksFragment newInstanceTime(int timeslotID) {
		TalksFragment fragment = new TalksFragment();
		Bundle args = new Bundle();
		args.putString(ARG_TAB_TYPE, TalksType.TIME.name());
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (!isCreated) {
			init();
			isCreated = true;
		}
		switch (talksType) {
			case ALL:
			case NOTIFICATION:
				ActionBar actionBar = getActivity().getActionBar();
				actionBar.setDisplayShowCustomEnabled(true);
				actionBar.setCustomView(R.layout.custom_ab_button);
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (talksType == TalksType.NOTIFICATION && talkItemsList != null) {
			talkItemsList.clear();
			talkItemsList.addAll(TalkItem.getNotificationTalkList(getMainActivity().getTalkItemsList(),
					getMainActivity().getNotifyMap()));
		}
		listAdapter.notifyDataSetChanged();
	}
	
	private void init() {
		try {
			talksType = TalksType.valueOf(getArguments().getString(ARG_TAB_TYPE));
		} catch (Exception e) {
			talksType = TalksType.ALL;
		}
		switch (talksType) {
			case ROOM:
				roomID = getArguments().getInt(ARG_ROOM_ID);
				talkItemsList = TalkItem.getRoomTalkList(getMainActivity().getTalkItemsList(),
						roomID,
						getArguments().getLong(ARG_DATE_MS));
				itemLayoutID = R.layout.talks_room_list_item;
				break;
			case TIME:
				timeslotID = getArguments().getInt(ARG_TIMESLOT_ID);
				talkItemsList = TalkItem.getTimeslotTalkList(getMainActivity().getTalkItemsList(), timeslotID);
				roomItemsList = getMainActivity().getRoomItemsList();
				itemLayoutID = R.layout.talks_time_list_item;
				break;
			case NOTIFICATION:
				FlurryAgent.logEvent("Notifications_watched");
				talkItemsList = TalkItem.getNotificationTalkList(getMainActivity().getTalkItemsList(),
						getMainActivity().getNotifyMap());
				Collections.sort(talkItemsList, new TimeComparator());
				itemLayoutID = R.layout.talks_notify_list_item;
				break;
			default:
				FlurryAgent.logEvent("Talks_watched");
				talkItemsList = new ArrayList<TalkItem>(getMainActivity().getTalkItemsList());
				Collections.sort(talkItemsList, new TopicComparator());
				roomItemsList = getMainActivity().getRoomItemsList();
				itemLayoutID = R.layout.talks_all_list_item;
				break;
		}
		speakerItemsList = getMainActivity().getSpeakerItemsList();
		timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
		mediumDateFormat = android.text.format.DateFormat.getMediumDateFormat(getActivity().getApplicationContext());
		is12HourFormat = !android.text.format.DateFormat.is24HourFormat(getActivity());
		listAdapter = new ItemAdapter();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (talksType == TalksType.ALL)
			getMainActivity().setDrawerIndicatorEnabled(true);
		
		final View rootView = inflater.inflate(R.layout.items_list_view, container, false);
		
		final ListView listViewTalks = (ListView) rootView;
		listViewTalks.addFooterView(Utils.getFooterView(getActivity()));
		listViewTalks.setFooterDividersEnabled(false);
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(TalkFragment.newInstance(talkItemsList.get(position).getId()), true);
			}
		});
		return rootView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (talksType == TalksType.ALL) {
			ActionBar actionBar = getActivity().getActionBar();
			actionBar.setCustomView(null);
			actionBar.setDisplayShowCustomEnabled(false);
		}
	}
	
	class ItemAdapter extends BaseAdapter {
		
		static final int	FORMAT_DATE_FLAGS	= DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
																| DateUtils.FORMAT_NO_YEAR;
		
		private class ViewHolder {
			public TextView		textTopic;
			public TextView		text3;
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
				switch (talksType) {
					case ALL:
					case NOTIFICATION:
						holder.text3 = (TextView) viewItem.findViewById(R.id.textTime);
						break;
					case ROOM:
						holder.textTimeStart = (TextView) viewItem.findViewById(R.id.textTimeStart);
						holder.textTimeEnd = (TextView) viewItem.findViewById(R.id.textTimeEnd);
						if (is12HourFormat) {
							LinearLayout linearLayoutTime = (LinearLayout) viewItem.findViewById(R.id.linearLayoutTime);
							linearLayoutTime.getLayoutParams().width = getResources().getDimensionPixelSize(R.dimen.width_12h);
						}
						break;
					case TIME:
						holder.text3 = (TextView) viewItem.findViewById(R.id.textRoom);
						break;
					default:
						break;
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
			switch (talksType) {
				case ALL:
					String roomName;
					try {
						roomName = " – " + RoomItem.getByID(talkItem.getRoomID(), roomItemsList).getName();
					} catch (ItemNotFoundException e1) {
						roomName = "";
					}
					holder.text3.setText(DateUtils.formatDateTime(getActivity().getApplicationContext(),
							talkItem.getStartTime().getTime(),
							FORMAT_DATE_FLAGS) + roomName);
					break;
				case NOTIFICATION:
					long alarmTime = Utils.getAlarmTime(talkItem.getStartTime().getTime());
					holder.text3.setText(DateUtils.formatDateTime(getActivity().getApplicationContext(),
							alarmTime,
							FORMAT_DATE_FLAGS));
					break;
				case ROOM:
					holder.textTimeStart.setText(timeFormat.format(talkItem.getStartTime()));
					holder.textTimeEnd.setText(timeFormat.format(talkItem.getEndTime()));
					break;
				case TIME:
					try {
						holder.text3.setText(RoomItem.getByID(talkItem.getRoomID(), roomItemsList).getName());
					} catch (ItemNotFoundException e) {
						// e.printStackTrace();
						holder.text3.setText("");
					}
					break;
				default:
					break;
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
