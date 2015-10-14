package io.scalac.degree.android.fragment;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;

import android.text.TextUtils;
import android.text.format.DateUtils;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;

import io.scalac.degree.items.RoomItem;
import io.scalac.degree.items.SpeakerItem;
import io.scalac.degree.items.TalkItem;
import io.scalac.degree.items.TalkItem.TimeComparator;
import io.scalac.degree.items.TalkItem.TopicComparator;
import io.scalac.degree.utils.ItemNotFoundException;
import io.scalac.degree.utils.Utils;
import io.scalac.degree33.R;

@EFragment(R.layout.items_list_view)
public class TalksFragment extends BaseFragment {

	private ItemAdapter listAdapter;
	ArrayList<TalkItem> talkItemsList;
	ArrayList<SpeakerItem> speakerItemsList;
	ArrayList<RoomItem> roomItemsList;
	DateFormat timeFormat;
	DateFormat mediumDateFormat;
	boolean is12HourFormat;
	TalksType talksType = TalksType.ALL;

	@FragmentArg String talksTypeEnumName;
	@FragmentArg int roomID;
	@FragmentArg int timeslotID;
	@FragmentArg long dateMs;

	int itemLayoutID;

	public enum TalksType {
		ALL, ROOM, TIME, NOTIFICATION
	}

	@AfterInject void afterInject() {
		talksType = TextUtils.isEmpty(talksTypeEnumName) ? TalksType.ALL
				: TalksType.valueOf(talksTypeEnumName);

		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (talksType == TalksType.NOTIFICATION && talkItemsList != null) {
			talkItemsList.clear();
			talkItemsList.addAll(TalkItem.getNotificationTalkList(dataSource.getTalkItemsList(),
					dataSource.getNotifyMap()));
		}
		listAdapter.notifyDataSetChanged();
	}

	private void init() {
		switch (talksType) {
			case ROOM:
				talkItemsList = TalkItem.getRoomTalkList(dataSource.getTalkItemsList(),
						roomID, dateMs);
				itemLayoutID = R.layout.talks_room_list_item;
				break;
			case TIME:
				talkItemsList = TalkItem.getTimeslotTalkList(dataSource.getTalkItemsList(), timeslotID);
				roomItemsList = dataSource.getRoomItemsList();
				itemLayoutID = R.layout.talks_time_list_item;
				break;
			case NOTIFICATION:
				logFlurryEvent("Notifications_watched");

				talkItemsList = TalkItem.getNotificationTalkList(dataSource.getTalkItemsList(),
						dataSource.getNotifyMap());
				Collections.sort(talkItemsList, new TimeComparator());
				itemLayoutID = R.layout.talks_notify_list_item;
				break;
			default:
				logFlurryEvent("Talks_watched");

				talkItemsList = new ArrayList<>(dataSource.getTalkItemsList());
				Collections.sort(talkItemsList, new TopicComparator());
				roomItemsList = dataSource.getRoomItemsList();
				itemLayoutID = R.layout.talks_all_list_item;
				break;
		}

		speakerItemsList = dataSource.getSpeakerItemsList();
		timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
		mediumDateFormat = android.text.format.DateFormat.getMediumDateFormat(getActivity().getApplicationContext());
		is12HourFormat = !android.text.format.DateFormat.is24HourFormat(getActivity());
		listAdapter = new ItemAdapter();
	}

	@Override public boolean needsToolbarSpinner() {
		return setupSpinnerVisibility();
	}

	@Override public int getTitle() {
		switch (talksType) {
			case ALL:
				return R.string.drawer_menu_talks_label;
			case NOTIFICATION:
				return R.string.drawer_menu_my_schedule_label;
			case ROOM:
			case TIME:
			default:
				return -1;
		}
	}

	@AfterViews void afterViews() {
		final ListView listViewTalks = (ListView) getView();
		listViewTalks.addFooterView(Utils.getFooterView(getActivity()));
		listViewTalks.setFooterDividersEnabled(false);
		listViewTalks.setAdapter(listAdapter);
		listViewTalks.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				getMainActivity().replaceFragment(TalkFragment_.builder()
						.talkID(talkItemsList.get(position).getId()).build(), true);
			}
		});
	}

	private boolean setupSpinnerVisibility() {
		switch (talksType) {
			case ALL:
			case NOTIFICATION:
				return false;
			case ROOM:
			case TIME:
			default:
				return true;
		}
	}

	class ItemAdapter extends BaseAdapter {

		static final int FORMAT_DATE_FLAGS = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
				| DateUtils.FORMAT_NO_YEAR;

		private class ViewHolder {
			public TextView textTopic;
			public TextView text3;
			public TextView textSpeaker;
			public TextView textTimeStart;
			public TextView textTimeEnd;
			public ImageButton imageButtonNotify;
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
						roomName = "  " + RoomItem.getByID(talkItem.getRoomID(), roomItemsList).getName();
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
			boolean isAlarmSet = dataSource.getNotifyMap().containsKey(String.valueOf(talkItem.getId()));
			holder.imageButtonNotify.setImageResource(isAlarmSet ? R.drawable.ic_action_alarm
					: R.drawable.ic_action_alarm_add);
			holder.imageButtonNotify.setTag(position);
			return viewItem;
		}

		private final AlarmOnClick alarmOnClick = new AlarmOnClick();

		private class AlarmOnClick implements OnClickListener {

			@Override
			public void onClick(View v) {
				int position = (Integer) v.getTag();
				TalkItem talkItem = talkItemsList.get(position);
				boolean isAlarmSet = dataSource.getNotifyMap().containsKey(String.valueOf(talkItem.getId()));
				if (isAlarmSet)
					Utils.unsetNotify(getActivity().getApplicationContext(), talkItem.getId());
				else
					Utils.setNotify(getActivity().getApplicationContext(), talkItem.getId(), talkItem.getStartTime()
							.getTime(), true);
				dataSource.setNotifyMap(Utils.getAlarms(getActivity().getApplicationContext()));
				listAdapter.notifyDataSetChanged();
			}
		}
	}
}
